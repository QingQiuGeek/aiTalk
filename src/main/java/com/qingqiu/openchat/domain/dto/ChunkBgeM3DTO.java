package com.qingqiu.openchat.domain.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChunkBgeM3DTO {
    private String id;

    private String kbId;

    private String docId;

    private String content;

    private MetaData metadata;

    private float[] embedding;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Data
    public static class MetaData {
    }
}
