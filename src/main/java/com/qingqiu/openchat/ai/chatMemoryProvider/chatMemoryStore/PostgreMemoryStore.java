package com.qingqiu.openchat.ai.chatMemoryProvider.chatMemoryStore;

import com.qingqiu.openchat.service.ChatMessageService;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: QingQiu
 * @date: 2025/5/4
 * @description:
 * TODO 实现基于postgresql的存储，区分每个用户的每个会话
 * TODO 控制每个会话的聊天长度，会话长度到达阈值，提示用户开启新会话
 */

@Slf4j
public class PostgreMemoryStore implements ChatMemoryStore {

  @Resource
  private ChatMessageService chatMessageService;

  /**
   * 根据会话ID获取历史消息
   * 转换为LangChain4j的ChatMessage格式
   */
  @Override
  public List<ChatMessage> getMessages(Object memoryId) {
    try {
      if (memoryId == null) {
        return new ArrayList<>();
      }

      // 从数据库获取该会话的所有消息
      return chatMessageService.selectBySessionId(memoryId.toString());
    } catch (Exception e) {
      log.error("获取会话 {} 的消息失败: {}", memoryId, e.getMessage(), e);
      return new ArrayList<>();
    }
  }

  /**
   * 更新会话的消息历史
   */
  @Override
  public void updateMessages(Object memoryId, List<ChatMessage> messages) {
    try {
      if (memoryId == null || messages == null || messages.isEmpty()) {
        return;
      }
      Long sessionId = Long.parseLong(memoryId.toString());
      log.debug("成功更新会话 {} 的消息，共 {} 条", sessionId, messages.size());
    } catch (Exception e) {
      log.error("更新会话 {} 的消息失败: {}", memoryId, e.getMessage(), e);
    }
  }

  /**
   * 删除会话的所有消息
   */
  @Override
  public void deleteMessages(Object memoryId) {
    try {
      if (memoryId == null) {
        return;
      }
      chatMessageService.deleteBySessionId(memoryId.toString());

      log.info("成功删除会话 {} 的所有消息", memoryId.toString());
    } catch (Exception e) {
      log.error("删除会话 {} 的消息失败: {}", memoryId, e.getMessage(), e);
    }
  }


}