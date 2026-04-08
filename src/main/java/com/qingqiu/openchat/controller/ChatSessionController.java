package com.qingqiu.openchat.controller;

import com.qingqiu.openchat.domain.request.CreateChatSessionRequest;
import com.qingqiu.openchat.domain.request.UpdateChatSessionRequest;
import com.qingqiu.openchat.domain.vo.ChatSessionVO;
import com.qingqiu.openchat.service.ChatSessionService;
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

/**
 * 会话管理，不处理消息
 */
@RestController
@RequestMapping("/chat-sessions")
public class ChatSessionController {

    @Resource
    private ChatSessionService chatSessionService;

    // 查询所有聊天会话
    @GetMapping
    public R<List<ChatSessionVO>> getChatSessions() {
        return R.ok(chatSessionService.getChatSessions());
    }

    // 查询单个聊天会话
    @GetMapping("/{chatSessionId}")
    public R<ChatSessionVO> getChatSession(@PathVariable String chatSessionId) {
        return R.ok(chatSessionService.getChatSession(chatSessionId));
    }

    // 根据 agentId 查询聊天会话
    @GetMapping("/agent/{agentId}")
    public R<List<ChatSessionVO>> getChatSessionsByAgentId(@PathVariable String agentId) {
        return R.ok(chatSessionService.getChatSessionsByAgentId(agentId));
    }

    // 创建聊天会话
    @PostMapping
    public R<String> createChatSession(@RequestBody CreateChatSessionRequest request) {
        return R.ok(chatSessionService.createChatSession(request));
    }

    // 删除聊天会话
    @DeleteMapping("/{chatSessionId}")
    public R<Boolean> deleteChatSession(@PathVariable String chatSessionId) {
        return R.ok(chatSessionService.deleteChatSession(chatSessionId));
    }

    // 更新聊天会话名
    @PutMapping("/{chatSessionId}")
    public R<Boolean> updateChatSession(@PathVariable String chatSessionId, @RequestBody UpdateChatSessionRequest request) {
        return R.ok( chatSessionService.updateChatSession(chatSessionId, request));
    }
}
