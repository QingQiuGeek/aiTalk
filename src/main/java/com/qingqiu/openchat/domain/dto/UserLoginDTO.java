package com.qingqiu.openchat.domain.dto;

import java.io.Serializable;
import lombok.Data;


/**
 * @author 懒大王Smile
 */
@Data
public class UserLoginDTO {

  /**
   * 密码,可用于登录
   */
  private String password;


  /**
   * 邮箱
   */
  private String mail;

}