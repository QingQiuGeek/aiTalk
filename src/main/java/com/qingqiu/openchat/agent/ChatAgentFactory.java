package com.qingqiu.openchat.agent;

import com.qingqiu.openchat.convert.AgentConverter;
import com.qingqiu.openchat.convert.ChatMessageConverter;
import com.qingqiu.openchat.convert.KnowledgeBaseConverter;
import com.qingqiu.openchat.domain.dto.AgentDTO;
import com.qingqiu.openchat.domain.dto.KnowledgeBaseDTO;
import com.qingqiu.openchat.domain.entity.Agent;
import com.qingqiu.openchat.domain.entity.ChatSession;
import com.qingqiu.openchat.domain.entity.KnowledgeBase;
import com.qingqiu.openchat.exception.BizException;
import com.qingqiu.openchat.mapper.AgentMapper;
import com.qingqiu.openchat.mapper.ChatMessageMapper;
import com.qingqiu.openchat.mapper.ChatSessionMapper;
import com.qingqiu.openchat.mapper.KnowledgeBaseMapper;
import com.qingqiu.openchat.service.ChatMessageService;
import com.qingqiu.openchat.service.SseService;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ChatAgentFactory {

    private final AgentMapper agentMapper;
    private final ChatSessionMapper chatSessionMapper;
    private final ChatMessageMapper chatMessageMapper;
    private final KnowledgeBaseMapper knowledgeBaseMapper;
    private final AgentConverter agentConverter;
    private final ChatMessageConverter chatMessageConverter;
    private final KnowledgeBaseConverter knowledgeBaseConverter;
    private final LangChainToolExecutor toolExecutor;
    private final SseService sseService;
    private final ChatMessageService chatMessageService;
    private final ChatModel chatModel;

    public ChatAgent create(Long agentId, String sessionId) {
        Agent agent = agentMapper.selectOneById(agentId);
        if (agent == null) {
            throw new BizException("Agent 不存在: " + agentId);
        }

        ChatSession chatSession = chatSessionMapper.selectById(sessionId);
        if (chatSession == null) {
            throw new BizException("聊天会话不存在: " + sessionId);
        }

        try {
            AgentDTO agentDTO = agentConverter.toDTO(agent);
            int maxMessages = agentDTO.getChatOptions() != null && agentDTO.getChatOptions().getMessageLength() != null
                ? agentDTO.getChatOptions().getMessageLength()
                : 20;

            List<ChatMessage> memory = new ArrayList<>();
            for (com.qingqiu.openchat.domain.entity.ChatMessage message : chatMessageMapper.selectBySessionIdRecently(sessionId, maxMessages)) {
                if (message == null || message.getRole() == null) {
                    continue;
                }
                if ("user".equalsIgnoreCase(message.getRole())) {
                    memory.add(new UserMessage(message.getContent()));
                } else if ("assistant".equalsIgnoreCase(message.getRole())) {
                    memory.add(new AiMessage(message.getContent()));
                } else if ("system".equalsIgnoreCase(message.getRole())) {
                    memory.add(SystemMessage.from(message.getContent()));
                }
            }
            List<KnowledgeBaseDTO> availableKbs = new ArrayList<>();
            if (agentDTO.getAllowedKbs() != null && !agentDTO.getAllowedKbs().isEmpty()) {
                List<Long> knowledgeBaseIds = agentDTO.getAllowedKbs();
                for (KnowledgeBase knowledgeBase : knowledgeBaseMapper.selectByIdBatch(knowledgeBaseIds)) {
                    availableKbs.add(knowledgeBaseConverter.toDTO(knowledgeBase));
                }
            }

            return new ChatAgent(
                agentId == null ? null : agentId.toString(),
                agentDTO.getName(),
                agentDTO.getDescription(),
                agentDTO.getSystemPrompt(),
                chatModel,
                maxMessages,
                memory,
                toolExecutor,
                availableKbs,
                sessionId,
                sseService,
                chatMessageService,
                chatMessageConverter
            );
        } catch (Exception e) {
            throw new BizException("创建 ChatAgent 失败: " + e.getMessage());
        }
    }
}