package com.qingqiu.openchat.factory;

import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel.GoogleAiEmbeddingModelBuilder;
import dev.langchain4j.model.googleai.GoogleAiGeminiBatchImageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiBatchImageModel.GoogleAiGeminiBatchImageModelBuilder;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel.GoogleAiGeminiChatModelBuilder;
import dev.langchain4j.model.googleai.GoogleAiGeminiImageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiImageModel.GoogleAiGeminiImageModelBuilder;
import dev.langchain4j.model.googleai.GoogleAiGeminiStreamingChatModel.GoogleAiGeminiStreamingChatModelBuilder;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel.OpenAiChatModelBuilder;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel.OpenAiEmbeddingModelBuilder;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.openai.OpenAiImageModel.OpenAiImageModelBuilder;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel.OpenAiStreamingChatModelBuilder;
import jakarta.annotation.Resource;

/**
 * @author: QingQiu
 * @date: 2025/7/15 12:05
 * @description: googleAIChatModel 工厂类
 */
public class GoogleAIChatModelFactory {

  @Resource
  GoogleAiGeminiStreamingChatModelBuilder streamingChatModelBuilder;

  @Resource
  GoogleAiGeminiChatModelBuilder chatModelBuilder;

  @Resource
  GoogleAiEmbeddingModelBuilder embeddingModelBuilder;

  @Resource
  GoogleAiGeminiBatchImageModelBuilder batchImageModelBuilder;

  @Resource
  GoogleAiGeminiImageModelBuilder imageModelBuilder;

  public GoogleAiGeminiStreamingChatModelBuilder createStreamingChatModel(){
    return null;
  }

  public GoogleAiGeminiChatModelBuilder createChatModel(){
    return null;
  }

  public GoogleAiEmbeddingModelBuilder createEmbeddingModel(){
    return null;
  }

  public GoogleAiGeminiBatchImageModel createGoogleAiGeminiBatchImageModel(){
    return null;
  }

  public GoogleAiGeminiImageModel createGoogleAiGeminiImageModel(){
    return null;
  }

}
