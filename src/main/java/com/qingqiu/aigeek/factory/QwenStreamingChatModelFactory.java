package com.qingqiu.aigeek.factory;

import dev.langchain4j.community.model.dashscope.QwenChatModel.QwenChatModelBuilder;
import dev.langchain4j.community.model.dashscope.QwenChatRequestParameters;
import dev.langchain4j.community.model.dashscope.QwenChatRequestParameters.SearchOptions;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel.QwenStreamingChatModelBuilder;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * @author: QingQiu
 * @date: 2025/7/15 12:05
 * @description: 生成qwenStreamingChatModel的工厂类
 */
@Component
public class QwenStreamingChatModelFactory {

  @Resource
  QwenStreamingChatModelBuilder textQwenStreamingChatModelBuilder;

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

    return textQwenStreamingChatModelBuilder
        .enableSearch(enableSearch)
        .defaultRequestParameters(paramBuilder.build())
        .build();
  }
}
