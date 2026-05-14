package com.qingqiu.openchat.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.qingqiu.openchat.convert.KnowledgeBaseConverter;
import com.qingqiu.openchat.domain.dto.KnowledgeBaseDTO;
import com.qingqiu.openchat.domain.entity.KnowledgeBase;
import com.qingqiu.openchat.domain.request.CreateKnowledgeBaseRequest;
import com.qingqiu.openchat.domain.request.UpdateKnowledgeBaseRequest;
import com.qingqiu.openchat.domain.vo.KnowledgeBaseVO;
import com.qingqiu.openchat.exception.BizException;
import com.qingqiu.openchat.service.KnowledgeBaseService;
import jakarta.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    @Resource
    private KnowledgeBaseConverter knowledgeBaseConverter;

    @Resource
    private JdbcTemplate jdbcTemplate;

    private KnowledgeBase mapKnowledgeBase(ResultSet resultSet) throws SQLException {
        return KnowledgeBase.builder()
            .id(resultSet.getLong("id"))
            .name(resultSet.getString("name"))
            .description(resultSet.getString("description"))
            .metadata(resultSet.getString("metadata"))
            .createdAt(resultSet.getTimestamp("created_at").toLocalDateTime())
            .updatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime())
            .build();
    }

    @Override
    public List<KnowledgeBaseVO> getKnowledgeBases() {
        List<KnowledgeBase> knowledgeBases = jdbcTemplate.query(
            "select id, name, description, metadata, created_at, updated_at from knowledge_base where is_deleted = 0 order by created_at desc",
            (resultSet, rowNum) -> mapKnowledgeBase(resultSet)
        );
        return knowledgeBases.stream().map(KnowledgeBase::convertToVO).toList();
    }

    @Override
    public String createKnowledgeBase(CreateKnowledgeBaseRequest request) {
        try {
            KnowledgeBaseDTO knowledgeBaseDTO = knowledgeBaseConverter.toDTO(request);
            KnowledgeBase knowledgeBase = knowledgeBaseConverter.toEntity(knowledgeBaseDTO);

            LocalDateTime now = LocalDateTime.now();
            knowledgeBase.setCreatedAt(now);
            knowledgeBase.setUpdatedAt(now);

            int result = jdbcTemplate.update(
                "insert into knowledge_base (name, description, metadata, created_at, updated_at, is_deleted) values (?, ?, ?, ?, ?, 0)",
                knowledgeBase.getName(),
                knowledgeBase.getDescription(),
                knowledgeBase.getMetadata(),
                Timestamp.valueOf(now),
                Timestamp.valueOf(now)
            );
            if (result <= 0) {
                throw new BizException("创建知识库失败");
            }

            String createdId = jdbcTemplate.query(
                "select id from knowledge_base where name = ? and created_at = ? order by created_at desc limit 1",
                resultSet -> resultSet.next() ? String.valueOf(resultSet.getLong(1)) : null,
                knowledgeBase.getName(),
                Timestamp.valueOf(now)
            );
            if (createdId == null) {
                throw new BizException("创建知识库失败");
            }

            return createdId;
        } catch (JsonProcessingException e) {
            throw new BizException("创建知识库时发生序列化错误: " + e.getMessage());
        }
    }

    @Override
    public Boolean deleteKnowledgeBase(String knowledgeBaseId) {
        KnowledgeBase knowledgeBase = jdbcTemplate.query(
            "select id, name, description, metadata, created_at, updated_at from knowledge_base where id = ? and is_deleted = 0 limit 1",
            resultSet -> resultSet.next() ? mapKnowledgeBase(resultSet) : null,
            Long.parseLong(knowledgeBaseId)
        );
        if (knowledgeBase == null) {
            throw new BizException("知识库不存在: " + knowledgeBaseId);
        }
        int result = jdbcTemplate.update("delete from knowledge_base where id = ?", Long.parseLong(knowledgeBaseId));
        if (result <= 0) {
            throw new BizException("删除知识库失败");
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean updateKnowledgeBase(UpdateKnowledgeBaseRequest request) {
        String knowledgeBaseId = request.getKnowledgeBaseId();
        if (StringUtils.isBlank(knowledgeBaseId)) {
            throw new BizException(500, "知识库ID不能为空");
        }
        try {
            KnowledgeBase existingKnowledgeBase = jdbcTemplate.query(
                "select id, name, description, metadata, created_at, updated_at from knowledge_base where id = ? and is_deleted = 0 limit 1",
                resultSet -> resultSet.next() ? mapKnowledgeBase(resultSet) : null,
                Long.parseLong(knowledgeBaseId)
            );
            if (existingKnowledgeBase == null) {
                throw new BizException("知识库不存在: " + knowledgeBaseId);
            }

            KnowledgeBaseDTO knowledgeBaseDTO = knowledgeBaseConverter.toDTO(existingKnowledgeBase);
            knowledgeBaseConverter.updateDTOFromRequest(knowledgeBaseDTO, request);
            KnowledgeBase updatedKnowledgeBase = knowledgeBaseConverter.toEntity(knowledgeBaseDTO);
            updatedKnowledgeBase.setId(existingKnowledgeBase.getId());
            updatedKnowledgeBase.setCreatedAt(existingKnowledgeBase.getCreatedAt());
            updatedKnowledgeBase.setUpdatedAt(LocalDateTime.now());

            int result = jdbcTemplate.update(
                "update knowledge_base set name = ?, description = ?, metadata = ?, updated_at = ? where id = ?",
                updatedKnowledgeBase.getName(),
                updatedKnowledgeBase.getDescription(),
                updatedKnowledgeBase.getMetadata(),
                Timestamp.valueOf(updatedKnowledgeBase.getUpdatedAt()),
                Long.parseLong(knowledgeBaseId)
            );
            if (result <= 0) {
                throw new BizException("更新知识库失败");
            }
        } catch (JsonProcessingException e) {
            throw new BizException("更新知识库时发生序列化错误: " + e.getMessage());
        }
        return Boolean.TRUE;
    }
}
