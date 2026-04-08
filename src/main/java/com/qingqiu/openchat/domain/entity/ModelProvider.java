package com.qingqiu.openchat.domain.entity;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * @author: Qing Qiu
 * @date: 2026/4/8 17:37
 * @description:
 */
@Data
public class ModelProvider {

  /**
   * 主键 ID (对应数据库 bigint)
   * 注意：如果数据库实际使用了 UUID，此处应改为 java.util.UUID
   */
  private Long modelId;

  /**
   * 所属用户 ID
   */
  private Long userId;

  /**
   * 实际模型标识 (如 "gpt-3.5-turbo", "qwen-turbo")
   */
  private String modelName;

  /**
   * 厂商类型 (如 "openai", "anthropic", "ollama", "custom")
   */
  private String providerType;

  /**
   * API 地址 (如 https://api.openai.com/v1)
   */
  private String baseUrl;

  /**
   * API Key (生产环境建议加密存储)
   */
  private String apiKey;

  /**
   * 该模型的最大上下文窗口
   */
  private Integer maxTokens;

  /**
   * 创建时间
   */
  private LocalDateTime createdAt;

  /**
   * 更新时间
   */
  private LocalDateTime updatedAt;

  /**
   * 逻辑删除标记 (0: 未删除, 1: 已删除)
   */
  private Boolean isDeleted;


}
