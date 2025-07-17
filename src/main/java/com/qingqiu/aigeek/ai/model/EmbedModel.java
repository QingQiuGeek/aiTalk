package com.qingqiu.aigeek.ai.model;

import com.qingqiu.aigeek.config.QwenModelConfig;
import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: QingQiu
 * @date: 2025/7/16 15:50
 * @description:
 */
@Configuration
public class EmbedModel {

  @Resource
  QwenModelConfig qwenModelConfig;

  /**
   * 嵌入向量模型
   */
  @Bean
  public EmbeddingModel embeddingModel() {
    return QwenEmbeddingModel.builder()
        .apiKey(qwenModelConfig.getEmbedModel().getApiKey())
        .modelName(qwenModelConfig.getEmbedModel().getModelName())
        .build();
  }
}
