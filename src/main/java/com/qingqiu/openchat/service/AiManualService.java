package com.qingqiu.openchat.service;

import com.qingqiu.openchat.ai.guardrail.SensitiveWordsInputGuardrail;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.guardrail.InputGuardrails;
import java.util.List;
import reactor.core.publisher.Flux;

/**
 * @Author: QingQiu
 * @Date: 2025/7/11
 * @Description:
 * ·@SystemMessage 和 @AiService 注解是配合使用的，如果没有@AiService并且手动实现该接口，那么@SystemMessage并不会生效，
 * 当然如果同时使用@Service和@AiService并且手动实现该接口，@SystemMessage仍会生效
 */

@InputGuardrails(SensitiveWordsInputGuardrail.class)
public interface AiManualService {

  /**
   * @memoryId 注解标记的参数会被框架识别为会话 ID，框架会根据这个 ID 来管理和存储聊天记录。每次调用 chatStr 方法时，框架会自动执行以下步骤：
   * 加载记忆：根据 sessionId 去数据库/内存里把历史聊天记录找出来。
   * 添加新消息：把新的UserMessage加到记录末尾。
   * 调用大模型：把这一整套记录发给模型。
   * 保存并返回：拿到 AI 的回答，把新对话存回数据库，然后回答返回。
   * @param sessionId
   * @param message
   * @return
   */
  String chatStr(@MemoryId String sessionId, @UserMessage String message);

  /*
  * aiService服务自动返回Result<List<String>>
  * */
  @SystemMessage("你是健身专家，仅回答健身领域知识")
  Result<List<String>> chatResult(@MemoryId String sessionId, String message);

  /*
  * 返回流式的
  * */
  @SystemMessage(fromResource = "prompt/prompt1.txt")
  TokenStream chatTokenStream(@MemoryId String sessionId, String message);

  /**
   * 流式调用
   */
  Flux<String> chatFlux(@MemoryId String sessionId, String message);

  Flux<String> chatFlux1(String message);

  String chatStr1(String message);

  /**
   * chatResponse
   */
  ChatResponse chatResponse(@MemoryId String sessionId, String message);
}
