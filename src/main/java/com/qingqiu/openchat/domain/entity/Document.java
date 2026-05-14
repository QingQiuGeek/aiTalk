package com.qingqiu.openchat.domain.entity;

import com.qingqiu.openchat.domain.vo.DocumentVO;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/**
 * @TableName document
 */
@Data
@Builder
public class Document {
    private Long id;

    private Long kbId;

    private String filename;

    private String filetype;

    private Long size;

    // JSON String
    private String metadata;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [" +
                "Hash = " + hashCode() +
                ", id=" + id +
                ", kbId=" + kbId +
                ", filename=" + filename +
                ", filetype=" + filetype +
                ", size=" + size +
                ", metadata=" + metadata +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                "]";
    }

    public static DocumentVO convertToVO(Document document) {
        if (document == null) {
            return null;
        }
        return DocumentVO.builder()
                .id(document.getId())
                .kbId(document.getKbId())
                .filename(document.getFilename())
                .filetype(document.getFiletype())
                .size(document.getSize())
                .build();
    }
}