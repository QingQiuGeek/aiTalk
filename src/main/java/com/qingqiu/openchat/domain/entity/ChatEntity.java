package com.qingqiu.openchat.domain.entity;

import dev.langchain4j.data.message.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: QingQiu
 * @date: 2025/7/22 14:35
 * @description:
 */

@AllArgsConstructor
@Data
public class ChatEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 会话id
   */
//  String chatId;

  /**
   * 会话的角色类型
   */
//  ChatMessageEnum type;

  /**
   * 会话内容
   */
  ChatMessage chatMessage;
}

