package com.qingqiu.openchat.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qingqiu.openchat.domain.SseMessage;
import com.qingqiu.openchat.manager.SseEmitterManager;
import com.qingqiu.openchat.service.SseService;
import com.qingqiu.openchat.util.UserContext;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SseServiceImpl implements SseService {

    //TODO 增加用户-会话id识别
    @Resource
    private SseEmitterManager MANAGER;

    @Override
    public SseEmitter connect(String chatSessionId) {
        return MANAGER.connect(UserContext.getUser(), chatSessionId);
    }

    @Override
    public void send(String chatSessionId, SseMessage message) {
        MANAGER.sendMessage(chatSessionId, message);
    }
}
