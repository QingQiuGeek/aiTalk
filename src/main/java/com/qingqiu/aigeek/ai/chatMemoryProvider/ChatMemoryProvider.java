package com.qingqiu.aigeek.ai.chatMemoryProvider;

import com.qingqiu.aigeek.ai.chatMemoryStore.FileMemoryStore;
import com.qingqiu.aigeek.ai.chatMemoryStore.RedisMemoryStore;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: QingQiu
 * @date: 2025/7/12 14:57
 * @description: 会话持久存储，ChatMemoryProvider动态创建每个会话的记忆,为每个 sessionId 创建独立实例,适用于多会话/多用户应用
 * TODO 确保每种存储方式都能实现
 */
@Data
@ConfigurationProperties(prefix = "chat-memory")
@Configuration
public class ChatMemoryProvider {

  private Integer maxMessages;

  private Integer maxTokens;

  /*
  * 基于内存的持久记忆
  * */
  @Bean
  dev.langchain4j.memory.chat.ChatMemoryProvider inMemoryStoreChatMemoryProvider(){
    return sessionId -> MessageWindowChatMemory.builder().chatMemoryStore(new InMemoryChatMemoryStore()).maxMessages(maxMessages).id(sessionId).build();
  }

  /*
  * 基于redis的持久记忆
  * */
  @Bean
  dev.langchain4j.memory.chat.ChatMemoryProvider redisStoreChatMemoryProvider(){
    return sessionId -> MessageWindowChatMemory.builder().chatMemoryStore(new RedisMemoryStore()).maxMessages(maxMessages).id(sessionId).build();
  }

  /*
  * 基于文件的持久记忆
  * */
  @Bean
  dev.langchain4j.memory.chat.ChatMemoryProvider fileStoreChatMemoryProvider(){
    return sessionId -> MessageWindowChatMemory.builder().chatMemoryStore(new FileMemoryStore()).maxMessages(maxMessages).id(sessionId).build();
  }

}
