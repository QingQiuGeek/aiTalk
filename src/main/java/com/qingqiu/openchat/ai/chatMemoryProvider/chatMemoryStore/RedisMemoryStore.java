package com.qingqiu.openchat.ai.chatMemoryProvider.chatMemoryStore;

import static com.qingqiu.openchat.constant.Common.CHAT_HISTORY;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author: QingQiu
 * @date: 2025/5/4
 * @description: 自定义实现redis存储,存储历史会话，和向量存储不一样
 * TODO 实现基于redis、postgresql的存储，区分每个用户的每个会话
 * TODO 控制每个会话的聊天长度，会话长度到达阈值，提示用户开启新会话
 */

public class RedisMemoryStore implements ChatMemoryStore {

  @Resource
  private StringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper = new ObjectMapper();

  /* ---------- 核心接口 ---------- */

  @Override
  public List<ChatMessage> getMessages(Object memoryId) {
    String key = CHAT_HISTORY + memoryId;
    List<String> jsonList = redisTemplate.opsForList().range(key, 0, -1);
    if (jsonList == null || jsonList.isEmpty()) {
      return List.of();
    }
    return jsonList.stream()
        .map(this::toChatMessage)
        .collect(Collectors.toList());
  }

  @Override
  public void updateMessages(Object memoryId, List<ChatMessage> messages) {
    String key = CHAT_HISTORY + memoryId;
    // 清空旧数据
    redisTemplate.delete(key);
    if (messages.isEmpty())
      return;

    // 批量追加新数据
    List<String> jsons = messages.stream()
        .map(this::toJson)
        .collect(Collectors.toList());
    redisTemplate.opsForList().rightPushAll(key, jsons);
    //设置有效期
//      redisTemplate.expire(key, CHAT_HISTORY_TTL, TimeUnit.DAYS);
  }

  @Override
  public void deleteMessages(Object memoryId) {
    redisTemplate.delete(CHAT_HISTORY + memoryId);
  }

  /* ---------- 序列化/反序列化 ---------- */

  private String toJson(ChatMessage msg) {
    try {
      return objectMapper.writeValueAsString(msg);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private ChatMessage toChatMessage(String json) {
    try {
      return objectMapper.readValue(json, ChatMessage.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}