package com.qingqiu.aigeek.service;

import com.qingqiu.aigeek.ai.guardrail.SensitiveWordsInputGuardrail;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.guardrail.InputGuardrails;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import java.util.List;
import reactor.core.publisher.Flux;

/**
 * @Author: QingQiu
 * @Date: 2025/7/11
 * @Description: @AiService注解可以实现自动调用model进行对话，就不需要手动实现AssistantService接口了，
 * 框架会帮我们自动实现，同时也会对返回的结果自动封装为Result<List<String>>，如果我们自己实现AssistantService接口的话还需要手动组装，两者各有优缺点
 * retrievalAugmentor检索增强器，是RAG入口的起点
 * contentRetriever内容检索器
 */
//敏感词检测
@InputGuardrails(value = SensitiveWordsInputGuardrail.class)
@AiService(wiringMode = AiServiceWiringMode.EXPLICIT
    , streamingChatModel = "textQwenStreamingChatModel"
    , chatModel = "textQwenChatModel"
    , retrievalAugmentor = "disableWebSearchQueryRouterRetrievalAugmentor"
    , chatMemoryProvider = "inMemoryStoreChatMemoryProvider"
    , tools = {"testTool", "scrapeWebPageTool","resourceDownloadTool"})
public interface AiAutoService {

  /**
   * @SystemMessage 设定角色，塑造AI助手的专业身份，明确助手的能力范围,@SystemMessage 的内容将在后台转换为 SystemMessage 对象，并与 UserMessage 一起发送给大语
   * 言模型（LLM）。SystemMessaged的内容只会发给大模型一次，如果修改了SystemMessage的内容，新的SystemMessage会被发送给大模型，之前的聊天记忆会失效。
   * @param userMessage
   * @return
   */
  @SystemMessage("你是一个顶级IT开发专家，仅回答IT领域的知识")
  String chatStr(String userMessage);

  ChatResponse chatResponse(String userMessage);

  @SystemMessage("你是健身专家，仅回答健身领域知识")
  Result<List<String>> chatResult(String userMessage);

  /**
   * 聊天流式输出
   * @param sessionId 会话id，通过@MemoryId指定
   * @param role 设定角色，通过@V注解替换掉system-message.txt中的role变量
   * @param question 原始问题，通过@V注解替换掉user-message.txt中的question变量
   * @param extraInfo 额外信息
   * @return
   */
  @SystemMessage(fromResource = "prompt/system-message.txt")
  @UserMessage(fromResource = "prompt/user-message.txt")
  Flux<String> chatFlux(
      @MemoryId String sessionId,
      @V("role") String role,
      @V("question") String question,
      @V("extraInfo") String extraInfo);

  @SystemMessage(fromResource = "prompt/system-message.txt")
  @UserMessage(fromResource = "prompt/user-message.txt")
  TokenStream chatTokenStream(
      @MemoryId String sessionId,
      @V("role") String role,
      @V("question") String question,
      @V("extraInfo") String extraInfo);
}
