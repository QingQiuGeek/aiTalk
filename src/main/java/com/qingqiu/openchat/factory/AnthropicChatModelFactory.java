package com.qingqiu.openchat.factory;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModel.AnthropicChatModelBuilder;
import dev.langchain4j.model.anthropic.AnthropicStreamingChatModel;
import dev.langchain4j.model.anthropic.AnthropicStreamingChatModel.AnthropicStreamingChatModelBuilder;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel.OpenAiChatModelBuilder;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel.OpenAiEmbeddingModelBuilder;
import dev.langchain4j.model.openai.OpenAiImageModel.OpenAiImageModelBuilder;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel.OpenAiStreamingChatModelBuilder;
import jakarta.annotation.Resource;

/**
 * @author: QingQiu
 * @date: 2025/7/15 12:05
 * @description: anthropicChatModel 工厂类
 */
public class AnthropicChatModelFactory {

  @Resource
  AnthropicStreamingChatModelBuilder streamingChatModelBuilder;

  @Resource
  AnthropicChatModelBuilder chatModelBuilder;

  public AnthropicStreamingChatModel createStreamingChatModel(){
    return null;
  }

  public AnthropicChatModel createChatModel(){
    return null;
  }




}
