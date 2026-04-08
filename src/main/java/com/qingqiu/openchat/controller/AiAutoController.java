package com.qingqiu.openchat.controller;

import static com.alibaba.dashscope.utils.JsonUtils.toJson;
import static com.qingqiu.openchat.convert.ContentConvert.convertToRecord;

import com.qingqiu.openchat.domain.dto.UserMessageDTO;
import com.qingqiu.openchat.service.AiAutoService;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.output.FinishReason;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import reactor.core.publisher.Sinks;

@Slf4j
@RestController
@RequestMapping("/ai/auto")
public class AiAutoController {

    @Resource
    AiAutoService aiAutoService;

    /**
     * @param message
     * @return
     */
    @GetMapping(value = "/chat/str")
    public String chatString1(String message) {
        return aiAutoService.chatStr(message);
    }

    /**
     * @param message
     * @return
     * TODO 返回result和返回流，返回流支持工具调用、rag、思考吗
     */
    @GetMapping(value = "/chat/result")
    public Result<List<String>> chatResult(String message) {
        Result<List<String>> listResult = aiAutoService.chatResult(message);
        List<Content> sources = listResult.sources();
        FinishReason finishReason = listResult.finishReason();
        List<ToolExecution> toolExecutions = listResult.toolExecutions();
        TokenUsage tokenUsage = listResult.tokenUsage();
        log.info("finishReason:{}",finishReason);
        log.info("tokenUsage:{}",tokenUsage);
        log.info("sources:{}",sources);
        log.info("toolExecutions:{}",toolExecutions);
        return listResult;
    }

    /**
     * tokenStream流式调用
     * @return
     */
    @GetMapping(value = "/chat/tokenStream")
    public Flux<ServerSentEvent<String>> chatTokenStream(@RequestBody UserMessageDTO userMessage) {
        TokenStream tokenStream = aiAutoService.chatTokenStream(userMessage.getSessionId(),userMessage.getRole(),userMessage.getUserMessage(),userMessage.getExtraInfo());
        // 注册流式处理器
        tokenStream.onPartialResponse(log::info)
            .onRetrieved((List<Content> contents) -> log.info(contents.toString()))
            .onToolExecuted((ToolExecution toolExecution) -> log.info(toolExecution.toString()))
            .onCompleteResponse((ChatResponse response) -> log.info(response.toString()))
            .onError(Throwable::printStackTrace)
            .start();
        Sinks.Many<ServerSentEvent<String>> sink = Sinks.many().unicast().onBackpressureBuffer();
        //rag回调
        tokenStream.onRetrieved(contents ->
            //前端可监听Retrieved时间，展示命中的文件
            sink.tryEmitNext(ServerSentEvent.builder(toJson(convertToRecord(contents))).event("Retrieved").build()));
        //消息片段回调
        tokenStream.onPartialResponse(partialResponse -> sink.tryEmitNext(ServerSentEvent.builder(partialResponse).event("AiMessage").build()));
        //错误回调
        tokenStream.onError(sink::tryEmitError);
        //结束回调
        tokenStream.onCompleteResponse(aiMessageResponse -> sink.tryEmitComplete());
        tokenStream.start();
        return sink.asFlux();
    }

    /**
     *  webflux 流式调用 AI
     * @return
     */
    @GetMapping(value = "/chat/server_sent_event")
    public Flux<ServerSentEvent<String>> chatServerSentEvent(@RequestBody UserMessageDTO userMessage) {
        Sinks.Many<ServerSentEvent<String>> sink = Sinks.many().unicast().onBackpressureBuffer();
        TokenStream tokenStream = aiAutoService.chatTokenStream(userMessage.getSessionId(),userMessage.getRole(),userMessage.getUserMessage(),userMessage.getExtraInfo());
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            tokenStream.onPartialResponse(response -> sink.tryEmitNext(ServerSentEvent.builder(response).build()))
                .onError(error -> {
                    sink.tryEmitError(error);
                    executorService.shutdown();
                })
                .onCompleteResponse(response -> {
                    sink.tryEmitComplete();
                    executorService.shutdown();
                })
                .start();
        });
        return sink.asFlux();
    }

    /**
     * 传统mvc SseEmitter ，SSE 流式调用 AI
     */
    @GetMapping(value = "/chat/sse")
    public SseEmitter chatSse(@RequestBody UserMessageDTO userMessage) {
        // 30 分钟不发送数据则超时
        SseEmitter sseEmitter = new SseEmitter(1800000L);
        // 获取 Flux 响应式数据流并且直接通过订阅推送给 SseEmitter
        aiAutoService.chatFlux(userMessage.getSessionId(), userMessage.getRole(), userMessage.getUserMessage(), userMessage.getExtraInfo())
                .subscribe(chunk -> {
                    try {
                        sseEmitter.send(chunk);
                    } catch (IOException e) {
                        sseEmitter.completeWithError(e);
                    }
                }, sseEmitter::completeWithError, sseEmitter::complete);
        return sseEmitter;
    }

//    @GetMapping(value = "/chat/sse")
//    public ResponseEntity<?> chatSse(@RequestBody UserMessageDTO userMessage) {
        // 30 分钟不发送数据则超时
//        SseEmitter sseEmitter = new SseEmitter(1800000L);
        // 获取 Flux 响应式数据流并且直接通过订阅推送给 SseEmitter
//        Flux<String> stream =  aiAutoService.chatFlux(userMessage.getSessionId(),
//                userMessage.getRole(), userMessage.getUserMessage(), userMessage.getExtraInfo())
//            .doOnNext(event -> {
//                if ("delta".equals(event.getType()) && event.getContent() != null) {
//                    responseContent.append(event.getContent());
//                }
//                if ("references".equals(event.getType()) && event.getReferences() != null) {
//                    responseReferences.set(event.getReferences());
//                }
//            })
//            .map(this::serializeStreamEvent)
//            .doOnComplete(() -> {
//                ChatMessage assistantMessage = ChatMessage.builder()
//                    .id(UUID.randomUUID().toString())
//                    .role("assistant")
//                    .content(responseContent.toString())
//                    .references(responseReferences.get())
//                    .timestamp(System.currentTimeMillis())
//                    .build();
//                conversationService.addMessage(finalConversationId, assistantMessage);
//                log.info("流式响应完成并保存: conversationId={}", finalConversationId);
//            });
//
//        return ResponseEntity.ok()
//            .contentType(MediaType.TEXT_EVENT_STREAM)
//            .body(stream);
//        return null;
//    }

    /**
     * Flux 流式调用 AI
     */
    @GetMapping(value = "/chat/flux")
    public Flux<String> chatFlux(@RequestBody UserMessageDTO userMessage) {
        return aiAutoService.chatFlux(userMessage.getSessionId(), userMessage.getRole(), userMessage.getUserMessage(), userMessage.getExtraInfo());
    }


}
