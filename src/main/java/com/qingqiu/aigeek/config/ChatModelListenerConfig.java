package com.qingqiu.aigeek.config;
import com.qingqiu.aigeek.util.IPUtil;
import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author: QingQiu
 * @date: 2025/7/11
 * @description: 自定义 Listener 获取 ChatModel 的调用信息,打印日志
 */

@Configuration
@Slf4j
public class ChatModelListenerConfig {

  @Bean
  ChatModelListener chatModelListener() {
    return new ChatModelListener() {
      @Override
      public void onRequest(ChatModelRequestContext requestContext) {
        log.info("ip: {},onRequest: {}",IPUtil.getIpAddr(), requestContext.chatRequest());
      }

      @Override
      public void onResponse(ChatModelResponseContext responseContext) {
        log.info("onResponse: {}", responseContext.chatResponse());
      }

      @Override
      public void onError(ChatModelErrorContext errorContext) {
        log.info("onError: {}", errorContext.error().getMessage());
      }
    };
}
}
