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
import java.util.List;
import reactor.core.publisher.Flux;

/**
 * @Author: QingQiu
 * @Date: 2025/7/11
 * @Description: @AiService注解可以实现自动调用model进行对话，就不需要手动实现AssistantService接口了，
 * 框架会帮我们自动实现，同时也会对返回的结果自动封装为Result<List<String>>，如果我们自己实现AssistantService接口的话还需要手动组装，两者各有优缺点
 *
 * @SystemMessage 和 @AiService 注解是配合使用的，如果没有@AiService并且手动实现该接口，那么@SystemMessage并不会生效，
 * 当然如果同时使用@Service和@AiService并且手动实现该接口，@SystemMessage仍会生效
 * TODO 测试同时使用@AiService和@Service，@SystemMessage是否生效
 */
@InputGuardrails(SensitiveWordsInputGuardrail.class)
public interface AiManualService {

  String chatStr(@MemoryId String sessionId,@UserMessage String message);

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
