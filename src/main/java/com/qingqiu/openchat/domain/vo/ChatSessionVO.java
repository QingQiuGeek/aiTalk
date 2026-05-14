package com.qingqiu.openchat.domain.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatSessionVO {
    private String id;
    private Long agentId;
    private String title;
}