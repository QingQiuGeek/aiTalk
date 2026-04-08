package com.qingqiu.openchat.util;

import cn.hutool.extra.spring.SpringUtil;
import com.qingqiu.openchat.domain.dto.SseEventDTO;
import com.qingqiu.openchat.domain.dto.SseMessageDTO;
import com.qingqiu.openchat.manager.SseEmitterManager;
import jakarta.annotation.Resource;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;

/**
 * SSE工具类
 *
 * @author Lion Li
 */
@Slf4j
public class SseMessageUtils {

    private static final SseEmitterManager MANAGER;

    static {
            MANAGER = SpringUtil.getBean(SseEmitterManager.class);
    }

    /**
     * 完成指定用户的SSE连接
     * 通过 Manager 断开连接并发送完成信号，自动触发资源清理
     *
     * @param userId 用户ID
     * @param tokenValue 用户 token 值
     */
    public static void completeConnection(Long userId, String tokenValue) {
        MANAGER.disconnect(userId, tokenValue);
    }

    /**
     * 向指定的SSE会话发送结构化事件
     *
     * @param userId   要发送消息的用户id
     * @param eventDto SSE事件对象
     */
    public static void sendEvent(Long userId, SseEventDTO eventDto) {
        MANAGER.sendEvent(userId, eventDto);
    }

    /**
     * 发送内容事件
     *
     * @param userId  用户ID
     * @param content 内容
     */
    public static void sendContent(Long userId, String content) {
        sendEvent(userId, SseEventDTO.content(content));
    }

    /**
     * 发送推理内容事件
     *
     * @param userId           用户ID
     * @param reasoningContent 推理内容
     */
    public static void sendReasoning(Long userId, String reasoningContent) {
        sendEvent(userId, SseEventDTO.reasoning(reasoningContent));
    }

    /**
     * 发送思考内容事件
     *
     * @param userId           用户ID
     * @param sendThinkingContent 思考内容
     */
    public static void sendThinking(Long userId, String sendThinkingContent) {
        sendEvent(userId, SseEventDTO.thinking(sendThinkingContent));
    }

    /**
     * 发送完成事件
     *
     * @param userId 用户ID
     */
    public static void sendDone(Long userId) {
        sendEvent(userId, SseEventDTO.done());
    }

    /**
     * 发送错误事件
     *
     * @param userId 用户ID
     * @param error  错误信息
     */
    public static void sendError(Long userId, String error) {
        sendEvent(userId, SseEventDTO.error(error));
    }

}
