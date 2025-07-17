package com.qingqiu.aigeek.ai.model;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam.ImageSynthesisParamBuilder;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam.MultiModalConversationParamBuilder;
import com.qingqiu.aigeek.config.QwenModelConfig;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenChatModel.QwenChatModelBuilder;
import dev.langchain4j.community.model.dashscope.QwenChatRequestParameters;
import dev.langchain4j.community.model.dashscope.QwenChatRequestParameters.SearchOptions;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel.QwenStreamingChatModelBuilder;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import jakarta.annotation.Resource;
import java.util.List;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: QingQiu
 * @date: 2025/7/11
 * @description: QwenChatModel，除了使用yml文件配置的（自动注入）外，还可以自己配置QwenChatModel
 */
@Configuration
@Data
public class QianWenChatModel {

  @Resource
  private ChatModelListener chatModelListener;

  @Resource
  private QwenModelConfig qwenModelConfig;


  /*
  * 普通文本模型 chatModel
  * 固定写死enableThinking和enableSearch，选择权不在用户，用户AiAutoService
  * */
  @Bean
  public QwenChatModel textQwenChatModel() {
    SearchOptions searchOptions = SearchOptions.builder()
        // 返回结果里是否带搜索来源
        .enableSource(true)
        // 是否开启上标引用
        .enableCitation(true)
        // 是否强制触发搜索
        .forcedSearch(false)
        .build();
    QwenChatRequestParameters parameters = QwenChatRequestParameters.builder()
        .enableSearch(true)
        .searchOptions(searchOptions)
        .build();
    return QwenChatModel.builder()
        .apiKey(qwenModelConfig.getTextModel().getApiKey())
        .modelName(qwenModelConfig.getTextModel().getModelName())
        .enableSearch(true)
        .defaultRequestParameters(parameters)
        .listeners(List.of(chatModelListener))
        .build();

  }

  /*
  * 流式文本模型 streamingChatModel
  * 固定写死enableThinking和enableSearch，选择权不在用户，用户AiAutoService
  * */
  @Bean
  public QwenStreamingChatModel textQwenStreamingChatModel() {
    SearchOptions searchOptions = SearchOptions.builder()
        // 返回结果里是否带搜索来源
        .enableSource(true)
        // 是否开启上标引用
        .enableCitation(true)
        // 是否强制触发搜索
        .forcedSearch(false)
        .build();
    QwenChatRequestParameters parameters = QwenChatRequestParameters.builder()
        .enableThinking(true)
        .isMultimodalModel(true)
        .enableSearch(true)
        .searchOptions(searchOptions)
        .build();
    return QwenStreamingChatModel.builder()
        .apiKey(qwenModelConfig.getTextModel().getApiKey())
        .modelName(qwenModelConfig.getEmbedModel().getModelName())
        .isMultimodalModel(true)
        .enableSearch(true)
        .defaultRequestParameters(parameters)
        .listeners(List.of(chatModelListener))
        .build();
  }

  /*
  * 文生图模型参数配置
  * */
  @Bean
  public ImageSynthesisParamBuilder textGenerateImageQwenChatModel() {
    return ImageSynthesisParam.builder()
            .apiKey(qwenModelConfig.getTextGenerateImageModel().getApiKey())
            .model(qwenModelConfig.getTextGenerateImageModel().getModelName())
            .prompt("")
            .negativePrompt("")
            .n(1)
            .size("1024*1024");
  }

  /*
   * 图生文模型参数配置
   * */
  @Bean
  public MultiModalConversationParamBuilder imageGenerateTextQwenChatModel() {
    return MultiModalConversationParam.builder()
        .apiKey(qwenModelConfig.getImageGenerateTextModel().getApiKey())
        .model(qwenModelConfig.getImageGenerateTextModel().getModelName());
  }

  /*
  * 通用 QwenChatModelBuilder，用于AiManualServiceImpl
  * 把「不会变」的公共配置（apiKey、modelName、listener）提前注入到 Builder 里，是否开启思考、联网搜索则根据用户的参数进行填写。
  * */
  @Bean
  public QwenChatModelBuilder textQwenChatModelBuilder() {
    return QwenChatModel.builder()
        .apiKey(qwenModelConfig.getTextModel().getApiKey())
        .modelName(qwenModelConfig.getTextModel().getModelName())
        .listeners(List.of(chatModelListener));
  }

  /**
   * 通用 QwenStreamingChatModelBuilder，用于AiManualServiceImpl
   * 把「不会变」的公共配置（apiKey、modelName、listener）提前注入到 Builder 里，是否开启联网搜索则根据用户的参数进行填写。
   */
  @Bean
  public QwenStreamingChatModelBuilder textQwenStreamingChatModelBuilder() {
    return QwenStreamingChatModel.builder()
        .apiKey(qwenModelConfig.getTextModel().getApiKey())
        .modelName(qwenModelConfig.getTextModel().getModelName())
        .listeners(List.of(chatModelListener));
  }

}
