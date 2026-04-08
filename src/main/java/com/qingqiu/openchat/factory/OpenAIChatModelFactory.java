package com.qingqiu.openchat.factory;

import dev.langchain4j.community.model.dashscope.QwenChatRequestParameters;
import dev.langchain4j.community.model.dashscope.QwenChatRequestParameters.SearchOptions;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel.QwenStreamingChatModelBuilder;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel.OpenAiChatModelBuilder;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel.OpenAiEmbeddingModelBuilder;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.openai.OpenAiImageModel.OpenAiImageModelBuilder;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel.OpenAiStreamingChatModelBuilder;
import dev.langchain4j.model.openai.spi.OpenAiStreamingChatModelBuilderFactory;
import jakarta.annotation.Resource;

/**
 * @author: QingQiu
 * @date: 2025/7/15 12:05
 * @description: openAIChatModel 工厂类
 */
public class OpenAIChatModelFactory {

  @Resource
  OpenAiStreamingChatModelBuilder streamingChatModelBuilder;

  @Resource
  OpenAiChatModelBuilder chatModelBuilder;

  @Resource
  OpenAiEmbeddingModelBuilder embeddingModelBuilder;

  @Resource
  OpenAiImageModelBuilder imageModelBuilder;

  public OpenAiStreamingChatModel createStreamingChatModel(){
    return null;
  }

  public OpenAiChatModel createChatModel(){
    return null;
  }

  public OpenAiEmbeddingModel createEmbeddingModel(){
    return null;
  }

  public OpenAiImageModel createImageModel(){
    return null;
  }



}
