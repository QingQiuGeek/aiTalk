package com.qingqiu.openchat.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.qingqiu.openchat.convert.ChatSessionConverter;
import com.qingqiu.openchat.domain.dto.ChatSessionDTO;
import com.qingqiu.openchat.domain.entity.ChatSession;
import com.qingqiu.openchat.domain.request.CreateChatSessionRequest;
import com.qingqiu.openchat.domain.request.UpdateChatSessionRequest;
import com.qingqiu.openchat.domain.vo.ChatSessionVO;
import com.qingqiu.openchat.exception.BizException;
import com.qingqiu.openchat.service.ChatSessionService;
import jakarta.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatSessionServiceImpl implements ChatSessionService {

    @Resource
    private ChatSessionConverter chatSessionConverter;

    @Resource
    private JdbcTemplate jdbcTemplate;

    private ChatSession mapChatSession(ResultSet resultSet) throws SQLException {
        return ChatSession.builder()
            .id(resultSet.getString("id"))
            .agentId(resultSet.getObject("agent_id") == null ? null : resultSet.getLong("agent_id"))
            .title(resultSet.getString("title"))
            .metadata(resultSet.getString("metadata"))
            .createdAt(resultSet.getTimestamp("created_at").toLocalDateTime())
            .updatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime())
            .build();
    }

    @Override
    public List<ChatSessionVO> getChatSessions() {
        List<ChatSession> chatSessionList = jdbcTemplate.query(
            "select id, agent_id, title, metadata, created_at, updated_at from chat_session where is_deleted = 0 order by created_at desc",
            (resultSet, rowNum) -> mapChatSession(resultSet)
        );
        return chatSessionList.stream().map(chatSession -> {
            try {
                return chatSessionConverter.toVO(chatSession);
            } catch (JsonProcessingException e) {
                throw new BizException("转换聊天会话失败: " + e.getMessage());
            }
        }).collect(Collectors.toList());
    }

    @Override
    public ChatSessionVO getChatSession(String chatSessionId) {
        if (StrUtil.isBlank(chatSessionId)) {
            throw new BizException("聊天会话ID不能为空");
        }
        ChatSession chatSession = jdbcTemplate.query(
            "select id, agent_id, title, metadata, created_at, updated_at from chat_session where id = ? and is_deleted = 0 limit 1",
            resultSet -> resultSet.next() ? mapChatSession(resultSet) : null,
            chatSessionId
        );
        if (chatSession == null) {
            throw new BizException("聊天会话不存在: " + chatSessionId);
        }
        try {
            return chatSessionConverter.toVO(chatSession);
        } catch (JsonProcessingException e) {
            throw new BizException("转换聊天会话失败: " + e.getMessage());
        }
    }

    @Override
    public List<ChatSessionVO> getChatSessionsByAgentId(String agentId) {
        List<ChatSession> chatSessionList = jdbcTemplate.query(
            "select id, agent_id, title, metadata, created_at, updated_at from chat_session where agent_id = ? and is_deleted = 0 order by created_at desc",
            (resultSet, rowNum) -> mapChatSession(resultSet),
            Long.parseLong(agentId)
        );
        if (CollectionUtil.isEmpty(chatSessionList)) {
            return Collections.emptyList();
        }
        return chatSessionList.stream().map(ChatSession::convertToVO).collect(Collectors.toList());
    }

    @Override
    public String createChatSession(CreateChatSessionRequest request) {
        ChatSessionDTO chatSessionDTO = chatSessionConverter.toDTO(request);
        ChatSession chatSession;
        try {
            chatSession = chatSessionConverter.toEntity(chatSessionDTO);
        } catch (JsonProcessingException e) {
            throw new BizException("创建聊天会话时发生序列化错误: " + e.getMessage());
        }

        LocalDateTime now = LocalDateTime.now();
        String sessionId = java.util.UUID.randomUUID().toString();
        chatSession.setId(sessionId);
        chatSession.setCreatedAt(now);
        chatSession.setUpdatedAt(now);

        int result = jdbcTemplate.update(
            "insert into chat_session (id, agent_id, title, metadata, created_at, updated_at) values (?, ?, ?, ?, ?, ?)",
            sessionId,
            chatSession.getAgentId(),
            chatSession.getTitle(),
            chatSession.getMetadata(),
            Timestamp.valueOf(now),
            Timestamp.valueOf(now)
        );
        if (result <= 0) {
            throw new BizException("创建聊天会话失败");
        }

        return sessionId;
    }

    @Override
    public Boolean deleteChatSession(String chatSessionId) {
        ChatSession chatSession = jdbcTemplate.query(
            "select id, agent_id, title, metadata, created_at, updated_at from chat_session where id = ? and is_deleted = 0 limit 1",
            resultSet -> resultSet.next() ? mapChatSession(resultSet) : null,
            chatSessionId
        );
        if (chatSession == null) {
            throw new BizException("聊天会话不存在: " + chatSessionId);
        }

        int result = jdbcTemplate.update("delete from chat_session where id = ?", chatSessionId);
        if (result <= 0) {
            throw new BizException("删除聊天会话失败");
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean updateChatSession(String chatSessionId, UpdateChatSessionRequest request) {
        try {
            ChatSession existingChatSession = jdbcTemplate.query(
                "select id, agent_id, title, metadata, created_at, updated_at from chat_session where id = ? and is_deleted = 0 limit 1",
                resultSet -> resultSet.next() ? mapChatSession(resultSet) : null,
                chatSessionId
            );
            if (existingChatSession == null) {
                throw new BizException("聊天会话不存在: " + chatSessionId);
            }

            ChatSessionDTO chatSessionDTO = chatSessionConverter.toDTO(existingChatSession);
            chatSessionConverter.updateDTOFromRequest(chatSessionDTO, request);
            ChatSession updatedChatSession = chatSessionConverter.toEntity(chatSessionDTO);
            updatedChatSession.setId(existingChatSession.getId());
            updatedChatSession.setAgentId(existingChatSession.getAgentId());
            updatedChatSession.setCreatedAt(existingChatSession.getCreatedAt());
            updatedChatSession.setUpdatedAt(LocalDateTime.now());

            int result = jdbcTemplate.update(
                "update chat_session set title = ?, metadata = ?, updated_at = ? where id = ?",
                updatedChatSession.getTitle(),
                updatedChatSession.getMetadata(),
                Timestamp.valueOf(updatedChatSession.getUpdatedAt()),
                chatSessionId
            );
            if (result <= 0) {
                throw new BizException("更新聊天会话失败");
            }
        } catch (JsonProcessingException e) {
            throw new BizException("更新聊天会话时发生序列化错误: " + e.getMessage());
        }
        return Boolean.TRUE;
    }
}