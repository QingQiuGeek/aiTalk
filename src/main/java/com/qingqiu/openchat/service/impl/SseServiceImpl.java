package com.qingqiu.openchat.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qingqiu.openchat.domain.SseMessage;
import com.qingqiu.openchat.service.SseService;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@AllArgsConstructor
public class SseServiceImpl implements SseService {

    //TODO 增加用户-会话id识别
    private final ConcurrentMap<String, SseEmitter> clients = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    @Override
    public SseEmitter connect(String chatSessionId) {
        //超时时间设为 30 分钟（30 * 60 * 1000 毫秒）。如果 30 分钟内没有发送任何数据，连接会自动超时断开。
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);
        clients.put(chatSessionId, emitter);

        try {
            //和前端协商
            emitter.send(SseEmitter.event()
                    .name("init")
                    .data("connected")
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //连接正常完成时调用，移除客户端
        emitter.onCompletion(() -> {
            clients.remove(chatSessionId);
        });
        //超时（30 分钟）时调用，移除客户端
        emitter.onTimeout(() -> clients.remove(chatSessionId));
        emitter.onError((error) -> clients.remove(chatSessionId));

        return emitter;
    }

    @Override
    public void send(String chatSessionId, SseMessage message) {
        SseEmitter emitter = clients.get(chatSessionId);

        if (emitter != null) {
            try {
                // 将消息转换为字符串
                String sseMessageStr = objectMapper.writeValueAsString(message);
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(sseMessageStr)
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("No client found for chatSessionId: " + chatSessionId);
        }
    }
}
