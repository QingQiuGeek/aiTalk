package com.qingqiu.aigeek.domain.dto;

import lombok.Data;

/**
 * @author: QingQiu
 * @date: 2025/7/15 12:55
 * @description: 用户消息DTO
 */
@Data
public class UserMessageDTO {

  /*
  * 用户消息，文本、图片等
  * */
  private String userMessage;

  /**
   * 会话id
   */
  private String sessionId;

  /**
   * 角色
   */
  private String role;

  /**
   * 额外信息
   */
  private String extraInfo;

  /*
  * 开启联网搜索
  * */
  private Boolean enableSearch;

  /**
   * 开启深度思考
   */
  private Boolean enableThinking;


}
