package com.qingqiu.openchat.service.impl;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.qingqiu.openchat.convert.ChatMessageConverter;
import com.qingqiu.openchat.domain.dto.ChatMessageDTO;
import com.qingqiu.openchat.domain.entity.ChatMessage;
import com.qingqiu.openchat.domain.entity.ChatSession;
import com.qingqiu.openchat.domain.request.CreateChatMessageRequest;
import com.qingqiu.openchat.domain.request.UpdateChatMessageRequest;
import com.qingqiu.openchat.domain.vo.ChatMessageVO;
import com.qingqiu.openchat.event.ChatEvent;
import com.qingqiu.openchat.exception.BizException;
import com.qingqiu.openchat.service.ChatMessageService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import jakarta.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    @Resource
    private ChatMessageConverter chatMessageConverter;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Resource
    private JdbcTemplate jdbcTemplate;

    private ChatMessage mapChatMessage(ResultSet resultSet) throws SQLException {
        return ChatMessage.builder()
            .id(resultSet.getString("id"))
            .sessionId(resultSet.getString("chat_session_id"))
            .role(resultSet.getString("role"))
            .content(resultSet.getString("content"))
            .metadata(resultSet.getString("metadata"))
            .createdAt(resultSet.getTimestamp("created_at").toLocalDateTime())
            .updatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime())
            .build();
    }

    @Override
    public List<ChatMessageVO> getChatMessagesBySessionId(String sessionId) {
        if (StrUtil.isBlank(sessionId)) {
            throw new BizException("会话ID不能为空");
        }
        List<ChatMessage> chatMessageList = jdbcTemplate.query(
            "select id, chat_session_id, role, content, metadata, created_at, updated_at from chat_message where chat_session_id = ? and is_deleted = 0 order by created_at asc",
            (resultSet, rowNum) -> mapChatMessage(resultSet),
            sessionId
        );
        return chatMessageList.stream().map(chatMessage -> {
            try {
                return chatMessageConverter.toVO(chatMessage);
            } catch (JsonProcessingException e) {
                throw new BizException("转换聊天消息失败: " + e.getMessage());
            }
        }).collect(Collectors.toList());
    }

    @Override
    public Boolean deleteBySessionId(String sessionId) {
        if (StrUtil.isBlank(sessionId)) {
            throw new BizException("会话ID不能为空");
        }
        int result = jdbcTemplate.update("delete from chat_message where chat_session_id = ?", sessionId);
        if (result < 0) {
            throw new BizException("删除聊天消息失败");
        }
        return Boolean.TRUE;
    }

    @Override
    public List<dev.langchain4j.data.message.ChatMessage> selectBySessionId(String sessionId) {
        if (StrUtil.isBlank(sessionId)) {
            throw new BizException("会话ID不能为空");
        }
        List<dev.langchain4j.data.message.ChatMessage> chatMessageList = new ArrayList<>();
        List<ChatMessage> messageList = jdbcTemplate.query(
            "select id, chat_session_id, role, content, metadata, created_at, updated_at from chat_message where chat_session_id = ? and is_deleted = 0 order by created_at asc",
            (resultSet, rowNum) -> mapChatMessage(resultSet),
            sessionId
        );
        for (ChatMessage chatMessageVo : messageList) {
            switch (chatMessageVo.getRole()) {
                case "user" -> chatMessageList.add(UserMessage.from(chatMessageVo.getContent()));
                case "assistant" -> chatMessageList.add(AiMessage.from(chatMessageVo.getContent()));
            }
        }
        return chatMessageList;
    }

    @Override
    public String createChatMessage(CreateChatMessageRequest request) {
        ChatMessageDTO dto = chatMessageConverter.toDTO(request);
        String response = createChatMessage(dto);

        if (dto.getRole() == ChatMessageDTO.RoleType.USER) {
            ChatSession chatSession = jdbcTemplate.query(
                "select id, agent_id, title, metadata, created_at, updated_at from chat_session where id = ? and is_deleted = 0 limit 1",
                resultSet -> {
                    if (!resultSet.next()) {
                        return null;
                    }
                    return ChatSession.builder()
                        .id(resultSet.getString("id"))
                        .agentId(resultSet.getObject("agent_id") == null ? null : resultSet.getLong("agent_id"))
                        .title(resultSet.getString("title"))
                        .metadata(resultSet.getString("metadata"))
                        .createdAt(resultSet.getTimestamp("created_at").toLocalDateTime())
                        .updatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime())
                        .build();
                },
                request.getSessionId()
            );
            if (chatSession != null) {
                applicationEventPublisher.publishEvent(new ChatEvent(
                    chatSession.getAgentId(),
                    request.getSessionId(),
                    request.getContent()
                ));
            }
        }

        return response;
    }

    @Override
    public String createChatMessage(ChatMessageDTO chatMessageDTO) {
        try {
            ChatMessage chatMessage = chatMessageConverter.toEntity(chatMessageDTO);
            String messageId = java.util.UUID.randomUUID().toString();
            chatMessage.setId(messageId);
            LocalDateTime now = LocalDateTime.now();
            chatMessage.setCreatedAt(now);
            chatMessage.setUpdatedAt(now);

            int result = jdbcTemplate.update(
                "insert into chat_message (id, chat_session_id, role, content, metadata, created_at, updated_at) values (?, ?, ?, ?, ?, ?, ?)",
                messageId,
                chatMessage.getSessionId(),
                chatMessage.getRole(),
                chatMessage.getContent(),
                chatMessage.getMetadata(),
                Timestamp.valueOf(now),
                Timestamp.valueOf(now)
            );
            if (result <= 0) {
                throw new BizException("创建聊天消息失败");
            }
            return messageId;
        } catch (JsonProcessingException e) {
            throw new BizException("创建聊天消息时发生序列化错误: " + e.getMessage());
        }
    }

    @Override
    public Boolean deleteChatMessage(String chatMessageId) {
        ChatMessage chatMessage = jdbcTemplate.query(
            "select id, chat_session_id, role, content, metadata, created_at, updated_at from chat_message where id = ? and is_deleted = 0 limit 1",
            resultSet -> resultSet.next() ? mapChatMessage(resultSet) : null,
            chatMessageId
        );
        if (chatMessage == null) {
            throw new BizException("聊天消息不存在: " + chatMessageId);
        }

        int result = jdbcTemplate.update("delete from chat_message where id = ?", chatMessageId);
        if (result <= 0) {
            throw new BizException("删除聊天消息失败");
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean updateChatMessage(String chatMessageId, UpdateChatMessageRequest request) {
        try {
            ChatMessage existingChatMessage = jdbcTemplate.query(
                "select id, chat_session_id, role, content, metadata, created_at, updated_at from chat_message where id = ? and is_deleted = 0 limit 1",
                resultSet -> resultSet.next() ? mapChatMessage(resultSet) : null,
                chatMessageId
            );
            if (existingChatMessage == null) {
                throw new BizException("聊天消息不存在: " + chatMessageId);
            }

            ChatMessageDTO chatMessageDTO = chatMessageConverter.toDTO(existingChatMessage);
            chatMessageConverter.updateDTOFromRequest(chatMessageDTO, request);

            ChatMessage updatedChatMessage = chatMessageConverter.toEntity(chatMessageDTO);
            updatedChatMessage.setId(existingChatMessage.getId());
            updatedChatMessage.setSessionId(existingChatMessage.getSessionId());
            updatedChatMessage.setCreatedAt(existingChatMessage.getCreatedAt());
            updatedChatMessage.setUpdatedAt(LocalDateTime.now());

            int result = jdbcTemplate.update(
                "update chat_message set content = ?, metadata = ?, updated_at = ? where id = ?",
                updatedChatMessage.getContent(),
                updatedChatMessage.getMetadata(),
                Timestamp.valueOf(updatedChatMessage.getUpdatedAt()),
                chatMessageId
            );
            if (result <= 0) {
                throw new BizException("更新聊天消息失败");
            }
        } catch (JsonProcessingException e) {
            throw new BizException("更新聊天消息时发生序列化错误: " + e.getMessage());
        }
        return Boolean.TRUE;
    }
}