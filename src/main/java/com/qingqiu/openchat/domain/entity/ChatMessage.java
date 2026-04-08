package com.qingqiu.openchat.domain.entity;

import com.qingqiu.openchat.domain.vo.ChatMessageVO;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/**
 * @TableName chat_message
 */
@Data
@Builder
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
        return null;
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