package com.qingqiu.openchat.service.impl;

import cn.hutool.core.util.IdUtil;
import com.qingqiu.openchat.domain.entity.ModelProvider;
import com.qingqiu.openchat.domain.request.CreateModelProviderRequest;
import com.qingqiu.openchat.domain.request.QueryModelProviderRequest;
import com.qingqiu.openchat.domain.request.UpdateModelProviderRequest;
import com.qingqiu.openchat.domain.vo.ModelProviderVO;
import com.qingqiu.openchat.service.ModelProviderService;
import com.qingqiu.openchat.util.UserContext;
import jakarta.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ModelProviderServiceImpl implements ModelProviderService {

  @Resource
  private JdbcTemplate jdbcTemplate;

  private ModelProvider mapModelProvider(ResultSet resultSet) throws SQLException {
    ModelProvider modelProvider = new ModelProvider();
    modelProvider.setId(resultSet.getLong("id"));
    modelProvider.setUserId(resultSet.getLong("user_id"));
    modelProvider.setModelName(resultSet.getString("model_name"));
    modelProvider.setProviderType(resultSet.getString("provider_type"));
    modelProvider.setBaseUrl(resultSet.getString("base_url"));
    modelProvider.setApiKey(resultSet.getString("api_key"));
    modelProvider.setMaxTokens(resultSet.getObject("max_tokens") == null ? null : resultSet.getInt("max_tokens"));
    modelProvider.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
    modelProvider.setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime());
    modelProvider.setIsDeleted(resultSet.getInt("is_deleted") == 1);
    return modelProvider;
  }

  @Override
  public Boolean delete(Long id) {
    Integer count = jdbcTemplate.query(
        "select count(*) from model_provider where id = ? and is_deleted = 0",
        resultSet -> resultSet.next() ? resultSet.getInt(1) : 0,
        id
    );
    if (count == null || count == 0) {
      return Boolean.FALSE;
    }
    return jdbcTemplate.update("delete from model_provider where id = ?", id) > 0;
  }

  @Override
  public List<ModelProviderVO> query(QueryModelProviderRequest queryModelProviderRequest) {
    StringBuilder sql = new StringBuilder("select id, user_id, model_name, provider_type, base_url, api_key, max_tokens, created_at, updated_at, is_deleted from model_provider where is_deleted = 0");
    List<Object> parameters = new ArrayList<>();
    if (queryModelProviderRequest != null) {
      if (queryModelProviderRequest.getId() != null) {
        sql.append(" and id = ?");
        parameters.add(queryModelProviderRequest.getId());
      }
      if (queryModelProviderRequest.getUserId() != null) {
        sql.append(" and user_id = ?");
        parameters.add(queryModelProviderRequest.getUserId());
      }
      if (queryModelProviderRequest.getModelName() != null) {
        sql.append(" and model_name like ?");
        parameters.add("%" + queryModelProviderRequest.getModelName() + "%");
      }
      if (queryModelProviderRequest.getProviderType() != null) {
        sql.append(" and provider_type = ?");
        parameters.add(queryModelProviderRequest.getProviderType());
      }
    }
    sql.append(" order by created_at desc");

    List<ModelProvider> providers = jdbcTemplate.query(sql.toString(), (resultSet, rowNum) -> mapModelProvider(resultSet), parameters.toArray());
    return providers.stream().map(this::toVO).toList();
  }

  @Override
  public Boolean update(UpdateModelProviderRequest updateModelProviderRequest) {
    if (updateModelProviderRequest == null || updateModelProviderRequest.getId() == null) {
      return Boolean.FALSE;
    }
    ModelProvider modelProvider = jdbcTemplate.query(
        "select id, user_id, model_name, provider_type, base_url, api_key, max_tokens, created_at, updated_at, is_deleted from model_provider where id = ? and is_deleted = 0 limit 1",
        resultSet -> resultSet.next() ? mapModelProvider(resultSet) : null,
        updateModelProviderRequest.getId()
    );
    if (modelProvider == null) {
      return Boolean.FALSE;
    }
    applyUpdate(modelProvider, updateModelProviderRequest);
    modelProvider.setUpdatedAt(LocalDateTime.now());
    return jdbcTemplate.update(
      "update model_provider set model_name = ?, provider_type = ?, base_url = ?, api_key = ?, max_tokens = ?, updated_at = ? where id = ?",
        modelProvider.getModelName(),
        modelProvider.getProviderType(),
        modelProvider.getBaseUrl(),
        modelProvider.getApiKey(),
        modelProvider.getMaxTokens(),
        Timestamp.valueOf(modelProvider.getUpdatedAt()),
        modelProvider.getId()
    ) > 0;
  }

  @Override
  public Boolean insert(CreateModelProviderRequest createModelProviderRequest) {
    if (createModelProviderRequest == null) {
      return Boolean.FALSE;
    }
    ModelProvider modelProvider = new ModelProvider();
    Long userId = createModelProviderRequest.getUserId();
    if (userId == null) {
      userId = UserContext.getUser();
    }
    modelProvider.setUserId(userId);
    applyCreate(modelProvider, createModelProviderRequest);
    LocalDateTime now = LocalDateTime.now();
    modelProvider.setCreatedAt(now);
    modelProvider.setUpdatedAt(now);
    modelProvider.setIsDeleted(Boolean.FALSE);

    return jdbcTemplate.update(
      "insert into model_provider (user_id, model_name, provider_type, base_url, api_key, max_tokens, created_at, updated_at, is_deleted) values (?, ?, ?, ?, ?, ?, ?, ?, ?)",
        modelProvider.getUserId(),
        modelProvider.getModelName(),
        modelProvider.getProviderType(),
        modelProvider.getBaseUrl(),
        modelProvider.getApiKey(),
        modelProvider.getMaxTokens(),
        Timestamp.valueOf(now),
        Timestamp.valueOf(now),
        0
    ) > 0;
  }

  private ModelProviderVO toVO(ModelProvider modelProvider) {
    ModelProviderVO vo = new ModelProviderVO();
    vo.setId(modelProvider.getId());
    vo.setUserId(modelProvider.getUserId());
    vo.setModelName(modelProvider.getModelName());
    vo.setProviderType(modelProvider.getProviderType());
    vo.setBaseUrl(modelProvider.getBaseUrl());
    vo.setApiKey(modelProvider.getApiKey());
    vo.setMaxTokens(modelProvider.getMaxTokens());
    vo.setCreatedAt(modelProvider.getCreatedAt());
    vo.setUpdatedAt(modelProvider.getUpdatedAt());
    vo.setIsDeleted(modelProvider.getIsDeleted());
    return vo;
  }

  private void applyCreate(ModelProvider modelProvider, CreateModelProviderRequest request) {
    modelProvider.setModelName(request.getModelName());
    modelProvider.setProviderType(request.getProviderType());
    modelProvider.setBaseUrl(request.getBaseUrl());
    modelProvider.setApiKey(request.getApiKey());
    modelProvider.setMaxTokens(request.getMaxTokens());
  }

  private void applyUpdate(ModelProvider modelProvider, UpdateModelProviderRequest request) {
    if (request.getModelName() != null) {
      modelProvider.setModelName(request.getModelName());
    }
    if (request.getProviderType() != null) {
      modelProvider.setProviderType(request.getProviderType());
    }
    if (request.getBaseUrl() != null) {
      modelProvider.setBaseUrl(request.getBaseUrl());
    }
    if (request.getApiKey() != null) {
      modelProvider.setApiKey(request.getApiKey());
    }
    if (request.getMaxTokens() != null) {
      modelProvider.setMaxTokens(request.getMaxTokens());
    }
  }
}
