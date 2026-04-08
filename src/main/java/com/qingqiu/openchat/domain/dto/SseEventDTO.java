package com.qingqiu.openchat.domain.dto;

import com.qingqiu.openchat.enums.ChatEventType;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SSE 事件数据传输对象
 * <p>
 * 标准的 SSE 消息格式，支持不同事件类型
 *
 * @author ageerle@163.com
 * @date 2025/03/19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SseEventDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 事件类型
     */
    private String event;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 推理内容
     */
    private String reasoning;

    /**
     * 深度思考模式
     */
    private String thinking;

    /**
     * 错误信息
     */
    private String error;

    /**
     * 是否完成
     */
    private Boolean done;

    /**
     * 创建内容事件
     */
    public static SseEventDTO content(String content) {
        return SseEventDTO.builder()
            .event(ChatEventType.CONTENT.getChatEventType())
            .content(content)
            .build();
    }

    /**
     * 创建推理内容事件
     */
    public static SseEventDTO reasoning(String reasoningContent) {
        return SseEventDTO.builder()
            .event(ChatEventType.REASONING.getChatEventType())
            .reasoning(reasoningContent)
            .build();
    }

    /**
     * 创建思考内容事件
     */
    public static SseEventDTO thinking(String thinkingContent) {
        return SseEventDTO.builder()
            .event(ChatEventType.THINKING.getChatEventType())
            .reasoning(thinkingContent)
            .build();
    }

    /**
     * 创建完成事件
     */
    public static SseEventDTO done() {
        return SseEventDTO.builder()
            .event(ChatEventType.DONE.getChatEventType())
            .done(true)
            .build();
    }

    /**
     * 创建错误事件
     */
    public static SseEventDTO error(String error) {
        return SseEventDTO.builder()
            .event(ChatEventType.ERROR.getChatEventType())
            .error(error)
            .build();
    }
}