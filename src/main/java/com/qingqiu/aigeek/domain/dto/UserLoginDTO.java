package com.qingqiu.aigeek.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;


/**
 * @author 懒大王Smile
 */
@Data
public class UserLoginDTO implements Serializable {


  //登陆凭证
  @TableField(exist = false)
  private String token;

  /**
   * 密码,可用于登录
   */
  private String password;


  /**
   * 邮箱
   */
  private String mail;

  @TableField(exist = false)
  private static final long serialVersionUID = 1L;
}