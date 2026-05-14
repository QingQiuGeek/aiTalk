package com.qingqiu.openchat.domain.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChunkBgeM3DTO {
    private Long id;

    private Long kbId;

    private Long docId;

    private String content;

    private MetaData metadata;

    private float[] embedding;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Data
    public static class MetaData {
    }
}
