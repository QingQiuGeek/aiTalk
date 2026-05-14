package com.qingqiu.openchat.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.qingqiu.openchat.domain.vo.ChatMessageVO;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/**
 * @TableName chat_message
 */
@Data
@Builder
@Table(value = "chat_message")
public class ChatMessage {
    private String id;

    private String sessionId;

    private String role;

    private String content;

    // JSON String
    private String metadata;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static ChatMessageVO convertToVO(ChatMessage chatMessage) {
        if (chatMessage == null) {
            return null;
        }
        return ChatMessageVO.builder()
            .id(chatMessage.getId())
            .sessionId(chatMessage.getSessionId())
                .content(chatMessage.getContent())
                .build();
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
            "content='" + content + '\'' +
            ", id='" + id + '\'' +
            ", sessionId='" + sessionId + '\'' +
            ", role='" + role + '\'' +
            ", metadata='" + metadata + '\'' +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}