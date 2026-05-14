package com.qingqiu.openchat.service;

import com.qingqiu.openchat.domain.request.CreateChatSessionRequest;
import com.qingqiu.openchat.domain.request.UpdateChatSessionRequest;
import com.qingqiu.openchat.domain.vo.ChatSessionVO;
import java.util.List;

public interface ChatSessionService {

    List<ChatSessionVO> getChatSessions();

    ChatSessionVO getChatSession(String chatSessionId);

    List<ChatSessionVO> getChatSessionsByAgentId(String agentId);

    String createChatSession(CreateChatSessionRequest request);

    Boolean deleteChatSession(String chatSessionId);

    Boolean updateChatSession(String chatSessionId, UpdateChatSessionRequest request);
}