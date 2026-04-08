package com.qingqiu.openchat.factory;

import dev.langchain4j.community.model.dashscope.QwenChatModel.QwenChatModelBuilder;
import dev.langchain4j.community.model.dashscope.QwenChatRequestParameters;
import dev.langchain4j.community.model.dashscope.QwenChatRequestParameters.SearchOptions;
import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel.QwenStreamingChatModelBuilder;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author: QingQiu
 * @date: 2025/7/15 12:05
 * @description: 生成qwenStreamingChatModel的工厂类
 */
@Component
public class QwenChatModelFactory {

  @Resource
  QwenStreamingChatModelBuilder streamingChatModelBuilder;

  @Resource
  QwenChatModelBuilder chatModelBuilder;

  @Resource
  QwenEmbeddingModel embeddingModel;

  public QwenStreamingChatModel createQwenStreamingChatModel(Boolean enableSearch, Boolean enableThinking){
    QwenChatRequestParameters.Builder paramBuilder = QwenChatRequestParameters.builder()
        .enableThinking(enableThinking)
        .enableSearch(enableSearch);

    if (enableSearch) {
      SearchOptions searchOptions = SearchOptions.builder()
          .enableSource(true)
          .enableCitation(true)
          .forcedSearch(false)
          .build();
      paramBuilder.searchOptions(searchOptions);
    }

    return streamingChatModelBuilder
        .enableSearch(enableSearch)
        .defaultRequestParameters(paramBuilder.build())
        .build();
  }

}
