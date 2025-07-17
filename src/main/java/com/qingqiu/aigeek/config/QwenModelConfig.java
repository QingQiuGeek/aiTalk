package com.qingqiu.aigeek.config;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenChatModel.QwenChatModelBuilder;
import dev.langchain4j.community.model.dashscope.QwenChatRequestParameters;
import dev.langchain4j.community.model.dashscope.QwenChatRequestParameters.SearchOptions;
import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel.QwenStreamingChatModelBuilder;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import jakarta.annotation.Resource;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: QingQiu
 * @date: 2025/7/11
 * @description: QwenChatModel配置类，除了使用yml文件配置的（自动注入）外，还可以自己配置QwenChatModel
 */

@ConfigurationProperties(prefix = "langchain4j.community.dashscope")
@Configuration
@Data
public class QwenModelConfig {

  private TextGenerateImageModel textGenerateImageModel;

  private ImageGenerateTextModel imageGenerateTextModel;

  private EmbedModel embedModel;

  private TextModel textModel;

  @Data
  public static class TextModel{
    private String modelName;
    private String apiKey;
  }

  @Data
  public static class ImageGenerateTextModel{
    private String modelName;
    private String apiKey;
  }

  @Data
  public static class TextGenerateImageModel{
    private String modelName;
    private String apiKey;
  }

  @Data
  public static class EmbedModel{
    private String modelName;
    private String apiKey;
  }

}
