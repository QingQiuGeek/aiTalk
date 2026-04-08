package com.qingqiu.openchat.controller;

import com.qingqiu.openchat.service.SseService;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SseController 处理 SSE 连接，提供一个端点供前端连接以接收消息
 */
@RestController
@RequestMapping("/sse")
public class SseController {

    @Resource
    private SseService sseService;

    // 处理 sse 连接
    @RequestMapping(value = "/connect/{chatSessionId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@PathVariable String chatSessionId) {
        return sseService.connect(chatSessionId);
    }
}
