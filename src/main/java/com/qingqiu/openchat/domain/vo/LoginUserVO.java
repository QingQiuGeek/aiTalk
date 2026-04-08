package com.qingqiu.openchat.domain.vo;

import lombok.Data;


@Data
public class LoginUserVO {

  /**
   * 用户id
   */
  private Long userId;

  /**
   * 用户名
   */
  private String userName;

  /**
   * 邮箱
   */
  private String mail;

}
