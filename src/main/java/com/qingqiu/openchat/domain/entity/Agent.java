package com.qingqiu.openchat.domain.entity;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/**
 * @TableName agent
 */
@Data
@Builder
public class Agent {

    private Long agentId;

    private Long userId;

    private String name;

    private String description;

    private String systemPrompt;

    private String model;

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
                ", id=" + agentId +
                ", name=" + name +
                ", description=" + description +
                ", systemPrompt=" + systemPrompt +
                ", model=" + model +
                ", allowedTools=" + allowedTools +
                ", allowedKbs=" + allowedKbs +
                ", chatOptions=" + chatOptions +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                "]";
    }
}