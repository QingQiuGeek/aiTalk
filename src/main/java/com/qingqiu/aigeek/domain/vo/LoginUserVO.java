package com.qingqiu.aigeek.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import java.util.Date;
import lombok.Data;


@Data
public class LoginUserVO {

  //登陆凭证
  @TableField(exist = false)
  private String token;

  /**
   * 用户id
   */
  private Long userId;

  /**
   * 头像URL
   */
  private String avatarUrl;


  /**
   * ip地址
   */
  private String ipAddress;

  /**
   * 用户名
   */
  private String userName;

  /**
   * 邮箱
   */
  private String mail;

  /**
   * 创建时间
   */
  private Date createTime;

}
