package com.qingqiu.openchat.domain.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KnowledgeBaseVO {
    private Long id;
    private String name;
    private String description;
}