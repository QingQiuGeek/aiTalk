package com.qingqiu.aigeek.app;

import static com.alibaba.dashscope.utils.JsonUtils.toJson;
import static com.qingqiu.aigeek.convert.ContentConvert.convertToRecord;

import com.qingqiu.aigeek.factory.QwenStreamingChatModelFactory;
import com.qingqiu.aigeek.domain.dto.UserMessageDTO;
import com.qingqiu.aigeek.service.AiManualService;
import com.qingqiu.aigeek.tools.ImageGenerateTextTool;
import com.qingqiu.aigeek.tools.TextGenerateImageTool;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.TokenStream;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * @author: QingQiu
 * @date: 2025/7/15 13:17
 * @description:
 */

@Component
public class AiManualApp {

    @Autowired
    @Qualifier("enableWebSearchQueryRouterRetrievalAugmentor")
    private RetrievalAugmentor enableWebSearchQueryRouterRetrievalAugmentor;

    @Autowired
    @Qualifier("disableWebSearchQueryRouterRetrievalAugmentor")
    private RetrievalAugmentor disableWebSearchQueryRouterRetrievalAugmentor;

    @Resource(name = "inMemoryStoreChatMemoryProvider")
    ChatMemoryProvider chatMemoryProvider;

    @Resource
    QwenStreamingChatModelFactory qwenStreamingChatModelFactory;

    @Resource
    QwenChatModel chatModel;

    @Resource
    ImageGenerateTextTool  imageGenerateTextTool;

    @Resource
    TextGenerateImageTool textGenerateImageTool;
  @Autowired
  private StreamingChatModel streamingChatModel;

  /**
     * 联网搜索时获取引用
     */
    public List<Content> getResource(String topic){
      return null;
    }

  /**
   * 返回的string，因此调用chatModel
   * @param userMessage
   * @return
   */
  public String chatStr(UserMessageDTO userMessage) {
    AiManualService aiManualService = AiServices.builder(AiManualService.class)
        .chatMemoryProvider(chatMemoryProvider)
        .chatModel(chatModel)
        .retrievalAugmentor(disableWebSearchQueryRouterRetrievalAugmentor)
        .build();
    return aiManualService.chatStr(userMessage.getSessionId(),userMessage.getUserMessage());
    }

    public Result<List<String>> chatResult(UserMessageDTO userMessage) {
      AiManualService aiManualService = AiServices.builder(AiManualService.class)
          .chatMemoryProvider(chatMemoryProvider)
          .chatModel(chatModel)
          .retrievalAugmentor(disableWebSearchQueryRouterRetrievalAugmentor)
          .build();
      return aiManualService.chatResult(userMessage.getSessionId(),userMessage.getUserMessage());
    }

    public Flux<ServerSentEvent<String>> chatTokenStream(UserMessageDTO userMessage) {
      QwenStreamingChatModel qwenStreamingChatModel = qwenStreamingChatModelFactory.createQwenStreamingChatModel(
          userMessage.getEnableSearch(), userMessage.getEnableThinking());
      AiServices<AiManualService> aiManualService = AiServices.builder(
              AiManualService.class)
          .chatMemoryProvider(chatMemoryProvider)
          .streamingChatModel(qwenStreamingChatModel);
      if(userMessage.getEnableSearch()){
        aiManualService.retrievalAugmentor(enableWebSearchQueryRouterRetrievalAugmentor);
      }else {
        aiManualService.retrievalAugmentor(disableWebSearchQueryRouterRetrievalAugmentor);
      }
      TokenStream tokenStream = aiManualService.build().chatTokenStream(userMessage.getSessionId(),
          userMessage.getUserMessage());
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

    public Flux<String> chatFlux(UserMessageDTO userMessage) {
      QwenStreamingChatModel qwenStreamingChatModel = qwenStreamingChatModelFactory.createQwenStreamingChatModel(
          userMessage.getEnableSearch(), userMessage.getEnableThinking());
      AiServices<AiManualService> aiManualService = AiServices.builder(
              AiManualService.class)
          .chatMemoryProvider(chatMemoryProvider)
          .tools(textGenerateImageTool,imageGenerateTextTool)
          .streamingChatModel(qwenStreamingChatModel);
      if(userMessage.getEnableSearch()){
        aiManualService.retrievalAugmentor(enableWebSearchQueryRouterRetrievalAugmentor);
      }else {
       aiManualService.retrievalAugmentor(disableWebSearchQueryRouterRetrievalAugmentor);
      }
      return aiManualService.build().chatFlux(userMessage.getSessionId(),userMessage.getUserMessage());
    }

    public String chatStr1(String msg) {
      AiServices<AiManualService> aiManualService = AiServices.builder(
            AiManualService.class)
        .chatMemoryProvider(chatMemoryProvider)
        .tools(textGenerateImageTool,imageGenerateTextTool)
        .chatModel(chatModel)
        .streamingChatModel(streamingChatModel);
      return aiManualService.build().chatStr1(msg);
    }

  public Flux<String> chatFlux1(String msg) {
    AiServices<AiManualService> aiManualService = AiServices.builder(
            AiManualService.class)
        .chatMemoryProvider(chatMemoryProvider)
        .tools(textGenerateImageTool,imageGenerateTextTool)
        .chatModel(chatModel)
        .streamingChatModel(streamingChatModel);
    return aiManualService.build().chatFlux1(msg);
  }

  public Flux<String> chatResponse(UserMessageDTO userMessage) {
    QwenStreamingChatModel qwenStreamingChatModel = qwenStreamingChatModelFactory.createQwenStreamingChatModel(
        userMessage.getEnableSearch(), userMessage.getEnableThinking());
    AiServices<AiManualService> aiManualService = AiServices.builder(
            AiManualService.class)
        .chatMemoryProvider(chatMemoryProvider)
        .streamingChatModel(qwenStreamingChatModel);
    if(userMessage.getEnableSearch()){
      aiManualService.retrievalAugmentor(enableWebSearchQueryRouterRetrievalAugmentor);
    }else {
      aiManualService.retrievalAugmentor(disableWebSearchQueryRouterRetrievalAugmentor);
    }
    return aiManualService.build().chatFlux(userMessage.getSessionId(),userMessage.getUserMessage());
  }

}

