package com.qingqiu.openchat.domain.vo;

import com.qingqiu.openchat.domain.dto.AgentDTO;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AgentVO {
    private Long id;
    private String name;
    private String description;
    private String systemPrompt;
    private Long modelProviderId;
    private List<String> allowedTools;
    private List<Long> allowedKbs;
    private AgentDTO.ChatOptions chatOptions;
}