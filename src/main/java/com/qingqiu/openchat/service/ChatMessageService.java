package com.qingqiu.openchat.service;

import com.qingqiu.openchat.domain.dto.ChatMessageDTO;
import com.qingqiu.openchat.domain.request.CreateChatMessageRequest;
import com.qingqiu.openchat.domain.request.UpdateChatMessageRequest;
import com.qingqiu.openchat.domain.vo.ChatMessageVO;
import dev.langchain4j.data.message.ChatMessage;
import java.util.List;

public interface ChatMessageService {

    List<ChatMessageVO> getChatMessagesBySessionId(String sessionId);

    List<ChatMessage> selectBySessionId(String sessionId);

    Boolean deleteBySessionId(String sessionId);

    String createChatMessage(CreateChatMessageRequest request);

    String createChatMessage(ChatMessageDTO chatMessageDTO);

    Boolean deleteChatMessage(String chatMessageId);

    Boolean updateChatMessage(String chatMessageId, UpdateChatMessageRequest request);

}