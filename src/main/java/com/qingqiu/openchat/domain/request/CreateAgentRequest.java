package com.qingqiu.openchat.domain.request;

import com.qingqiu.openchat.domain.dto.AgentDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAgentRequest {
    private String name;
    private String description;
    private String systemPrompt;
    private Long modelProviderId;
    private List<String> allowedTools;
    private List<Long> allowedKbs;
    private AgentDTO.ChatOptions chatOptions;
}