package com.qingqiu.openchat.service;

import com.qingqiu.openchat.domain.request.CreateAgentRequest;
import com.qingqiu.openchat.domain.request.UpdateAgentRequest;
import com.qingqiu.openchat.domain.vo.AgentVO;
import java.util.List;

public interface AgentService {
    List<AgentVO> getAgents();

    Long createAgent(CreateAgentRequest request);

    Boolean deleteAgent(String agentId);

    Boolean updateAgent(UpdateAgentRequest request);
}
