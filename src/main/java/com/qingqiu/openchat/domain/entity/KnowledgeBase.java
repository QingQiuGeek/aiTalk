package com.qingqiu.openchat.domain.entity;

import com.mybatisflex.annotation.Table;
import com.qingqiu.openchat.domain.vo.KnowledgeBaseVO;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @TableName knowledge_base
 */
@Data
@Builder
@NoArgsConstructor
@Table(value = "knowledge_base")
@AllArgsConstructor
public class KnowledgeBase {
    private Long id;

    private String name;

    private String description;

    private String metadata;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static KnowledgeBaseVO convertToVO(KnowledgeBase knowledgeBase) {
        if (knowledgeBase == null) {
            return null;
        }
        return KnowledgeBaseVO.builder()
            .id(knowledgeBase.getId())
                .name(knowledgeBase.getName())
                .description(knowledgeBase.getDescription())
                .build();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [" +
                "Hash = " + hashCode() +
                ", id=" + id +
                ", name=" + name +
                ", description=" + description +
                ", metadata=" + metadata +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                "]";
    }
}