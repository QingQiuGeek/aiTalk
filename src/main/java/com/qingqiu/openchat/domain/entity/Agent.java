package com.qingqiu.openchat.domain.entity;

import com.mybatisflex.annotation.Column;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @TableName agent
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Agent {

    private Long id;

    private Long userId;

    private String name;

    private String description;

    private String systemPrompt;

    @Column("model_provider_id")
    private Long modelProviderId;

    // JSON String
    private String allowedTools;

    // JSON String
    private String allowedKbs;

    // JSON String
    private String chatOptions;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [" +
                "Hash = " + hashCode() +
                ", id=" + id +
                ", name=" + name +
                ", description=" + description +
                ", systemPrompt=" + systemPrompt +
                ", modelProviderId=" + modelProviderId +
                ", allowedTools=" + allowedTools +
                ", allowedKbs=" + allowedKbs +
                ", chatOptions=" + chatOptions +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                "]";
    }
}