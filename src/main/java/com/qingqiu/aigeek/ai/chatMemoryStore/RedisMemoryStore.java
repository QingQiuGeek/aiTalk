package com.qingqiu.aigeek.ai.chatMemoryStore;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @Author: QingQiu
 * @Date: 2025/5/4
 * @Description: 自定义实现redis存储,存储历史会话，和向量存储不一样
 * TODO 实现基于redis的存储，区分每个用户的每个会话
 */

@Component
public class RedisMemoryStore implements ChatMemoryStore {

  @Resource
  StringRedisTemplate stringRedisTemplate;

  Map<String, List<ChatMessage>> conversationHistory =new ConcurrentHashMap<>();

  @Override
  public List<ChatMessage> getMessages(Object memoryId) {
    return conversationHistory.putIfAbsent((String)memoryId, new ArrayList<>());
  }

  @Override
  public void updateMessages(Object memoryId, List<ChatMessage> messages) {
    conversationHistory.put((String)memoryId,messages);
  }

  @Override
  public void deleteMessages(Object memoryId) {
    conversationHistory.remove(memoryId);
  }
}
