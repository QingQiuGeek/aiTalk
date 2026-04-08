package com.qingqiu.openchat.controller;

import com.qingqiu.openchat.domain.request.CreateAgentRequest;
import com.qingqiu.openchat.domain.request.UpdateAgentRequest;
import com.qingqiu.openchat.domain.vo.AgentVO;
import com.qingqiu.openchat.service.AgentService;
import com.qingqiu.openchat.util.R;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agent")
public class AgentController {

    @Resource
    private AgentService agentService;

    // 查询 agents
    @GetMapping
    public R<List<AgentVO>> getAgents() {
        return R.ok(agentService.getAgents());
    }

    // 创建 agent
    @PostMapping
    public R<Long> createAgent(@RequestBody CreateAgentRequest request) {
        return R.ok(agentService.createAgent(request));
    }

    // 删除 agent
    @DeleteMapping("/{agentId}")
    public R<Boolean> deleteAgent(@PathVariable String agentId) {
        return R.ok(agentService.deleteAgent(agentId));
    }

    // 更新 agent
    @PutMapping
    public R<Boolean> updateAgent(@RequestBody UpdateAgentRequest request) {
        return R.ok(agentService.updateAgent(request));
    }
}
