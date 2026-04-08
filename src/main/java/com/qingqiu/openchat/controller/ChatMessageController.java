package com.qingqiu.openchat.controller;

import com.qingqiu.openchat.domain.request.CreateChatMessageRequest;
import com.qingqiu.openchat.domain.request.UpdateChatMessageRequest;
import com.qingqiu.openchat.domain.vo.ChatMessageVO;
import com.qingqiu.openchat.service.ChatMessageService;
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
 * 管理聊天消息
 * @author Qing Qiu
 */
@RestController
@RequestMapping("/chat-messages")
public class ChatMessageController {

    @Resource
    private ChatMessageService chatMessageService;

    // 根据 sessionId 查询聊天消息
    @GetMapping("/session/{sessionId}")
    public R<List<ChatMessageVO>> getChatMessagesBySessionId(@PathVariable String sessionId) {
        return R.ok(chatMessageService.getChatMessagesBySessionId(sessionId));
    }

    // 创建聊天消息
    @PostMapping
    public R<String> createChatMessage(@RequestBody CreateChatMessageRequest request) {
        return R.ok(chatMessageService.createChatMessage(request));
    }

    // 删除聊天消息
    @DeleteMapping("/{chatMessageId}")
    public R<Boolean> deleteChatMessage(@PathVariable String chatMessageId) {
        return R.ok(chatMessageService.deleteChatMessage(chatMessageId));
    }

    // 更新聊天消息
    @PutMapping("/{chatMessageId}")
    public R<Boolean> updateChatMessage(@PathVariable String chatMessageId, @RequestBody UpdateChatMessageRequest request) {
        return R.ok(chatMessageService.updateChatMessage(chatMessageId, request));
    }
}
