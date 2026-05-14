package com.qingqiu.openchat.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mybatisflex.core.query.QueryWrapper;
import com.qingqiu.openchat.convert.AgentConverter;
import com.qingqiu.openchat.exception.BizException;
import com.qingqiu.openchat.mapper.AgentMapper;
import com.qingqiu.openchat.domain.dto.AgentDTO;
import com.qingqiu.openchat.domain.entity.Agent;
import com.qingqiu.openchat.domain.request.CreateAgentRequest;
import com.qingqiu.openchat.domain.request.UpdateAgentRequest;
import com.qingqiu.openchat.domain.vo.AgentVO;
import com.qingqiu.openchat.service.AgentService;
import com.qingqiu.openchat.util.UserContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class AgentServiceImpl implements AgentService {

    private final AgentMapper agentMapper;
    private final AgentConverter agentConverter;

    @Override
    public List<AgentVO> getAgents() {
        Long userId = UserContext.getUser();
        List<Agent> agents = userId == null
                ? agentMapper.selectAll()
                : agentMapper.selectListByQuery(QueryWrapper.create().eq(Agent::getUserId, userId));
        List<AgentVO> result = new ArrayList<>();
        for (Agent agent : agents) {
            try {
                AgentVO vo = agentConverter.toVO(agent);
                result.add(vo);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    @Override
    public Long createAgent(CreateAgentRequest request) {
        try {
            // 将 CreateAgentRequest 转换为 AgentDTO
            AgentDTO agentDTO = agentConverter.toDTO(request);
            
            // 将 AgentDTO 转换为 Agent 实体
            Agent agent = agentConverter.toEntity(agentDTO);
            Long userId = UserContext.getUser();
            if (userId == null) {
                throw new BizException("未登录，无法创建 agent");
            }
            agent.setUserId(userId);
            
            // 设置创建时间和更新时间
            LocalDateTime now = LocalDateTime.now();
            agent.setCreatedAt(now);
            agent.setUpdatedAt(now);
            
            // 插入数据库，ID 由数据库自动生成
            int result = agentMapper.insert(agent);
            if (result <= 0) {
                throw new BizException("创建 agent 失败");
            }

            return agent.getId();
        } catch (JsonProcessingException e) {
            throw new BizException("创建 agent 时发生序列化错误: " + e.getMessage());
        }
    }

    @Override
    public Boolean deleteAgent(Long agentId) {
        int result = agentMapper.deleteById(agentId);
        if (result <= 0) {
            throw new BizException("删除 agent 失败");
        }

        return Boolean.TRUE;
    }

    @Override
    public Boolean updateAgent(UpdateAgentRequest request) {
        String agentId = request.getAgentId();
        if(ObjUtil.isNull(agentId)) {
            throw new BizException("Agent ID 不能为空");
        }
        try {
            // 查询现有的 agent
            Agent existingAgent = agentMapper.selectOneById(Long.parseLong(agentId));
            if (existingAgent == null) {
                throw new BizException("Agent 不存在: " + agentId);
            }
            
            // 将现有 Agent 转换为 AgentDTO
            AgentDTO agentDTO = agentConverter.toDTO(existingAgent);
            
            // 使用 UpdateAgentRequest 更新 AgentDTO
            agentConverter.updateDTOFromRequest(agentDTO, request);
            
            // 将更新后的 AgentDTO 转换回 Agent 实体
            Agent updatedAgent = agentConverter.toEntity(agentDTO);
            
            // 保留原有的 ID 和创建时间
            updatedAgent.setId(existingAgent.getId());
            updatedAgent.setCreatedAt(existingAgent.getCreatedAt());
            updatedAgent.setUpdatedAt(LocalDateTime.now());
            
            // 更新数据库
            int result = agentMapper.update(updatedAgent);
            if (result <= 0) {
                throw new BizException("更新 agent 失败");
            }

            return Boolean.TRUE;
        } catch (JsonProcessingException e) {
            throw new BizException("更新 agent 时发生序列化错误: " + e.getMessage());
        }
    }
}
