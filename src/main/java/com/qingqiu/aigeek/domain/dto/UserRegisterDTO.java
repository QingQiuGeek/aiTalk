package com.qingqiu.aigeek.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;



@Data
public class UserRegisterDTO implements Serializable {


  /**
   * 密码
   */
  private String password;

  /**
   * 重复输入密码
   */
  private String rePassword;


  /**
   * 用户名
   */
  private String userName;


  /**
   * 邮箱
   */
  private String mail;

  /**
   * 验证码
   */
  private String code;

  @TableField(exist = false)
  private static final long serialVersionUID = 1L;
}