package com.qingqiu.aigeek.controller;

import com.qingqiu.aigeek.app.AiManualApp;
import com.qingqiu.aigeek.domain.dto.UserMessageDTO;
import dev.langchain4j.model.output.FinishReason;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.tool.ToolExecution;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/aiManual")
public class AiManualController {
    
    @Resource
    AiManualApp  aiManualApp;

    /**
     * @return
     */
    @PostMapping(value = "/chat/str")
    public String chatStr(@RequestBody UserMessageDTO userMessage) {
        return aiManualApp.chatStr(userMessage);
    }

    /**
     * @return
     */
    @PostMapping(value = "/chat/result")
    public Result<List<String>> chatResult(@RequestBody UserMessageDTO userMessage) {
        Result<List<String>> listResult = aiManualApp.chatResult(userMessage);
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
    @PostMapping(value = "/chat/tokenStream")
    public Flux<ServerSentEvent<String>> chatTokenStream(@RequestBody UserMessageDTO userMessage) {
        return aiManualApp.chatTokenStream(userMessage);
    }

    /**
     *  流式调用 AI
     * @return
     */
    @PostMapping(value = "/chat/tokenStream2",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatTokenStream2(@RequestBody UserMessageDTO userMessage) {
        return aiManualApp.chatTokenStream(userMessage);
    }

    /**
     *  流式调用 AI
     * @return
     */
    @PostMapping(value = "/chat/server_sent_event",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatServerSentEvent(@RequestBody UserMessageDTO userMessage) {
        return aiManualApp.chatFlux(userMessage).map(data -> ServerSentEvent.builder(data).build());
    }

    /**
     * SSE 流式调用 AI
     */
    @PostMapping(value = "/chat/sse",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatSse(@RequestBody UserMessageDTO userMessage) {
        // 创建一个超时时间较长的 SseEmitter ,3 分钟超时
        SseEmitter sseEmitter = new SseEmitter(180000L);
        // 获取 Flux 响应式数据流并且直接通过订阅推送给 SseEmitter
        aiManualApp.chatFlux(userMessage)
                .subscribe(chunk -> {
                    try {
                        sseEmitter.send(chunk);
                    } catch (IOException e) {
                        sseEmitter.completeWithError(e);
                    }
                }, sseEmitter::completeWithError, sseEmitter::complete);
        return sseEmitter;
    }

    /**
     * Flux 流式调用 AI
     */
    @PostMapping(value = "/chat/flux",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatFlux(@RequestBody UserMessageDTO userMessage) {
        return aiManualApp.chatFlux(userMessage);
    }

    @GetMapping(value = "/chat/flux1",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatFlux1(String message) {
        return aiManualApp.chatFlux1(message);
    }

    @GetMapping(value = "/chat/str1")
    public String chatStr1(String message) {
        return aiManualApp.chatStr1(message);
    }


}
