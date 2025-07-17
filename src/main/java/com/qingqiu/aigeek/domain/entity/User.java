package com.qingqiu.aigeek.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户表
 *
 * @TableName user
 */
@TableName(value = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {

  /**
   * 用户ID
   */
  @TableId(type = IdType.AUTO)
  private Long userId;

  /**
   * 头像URL
   */
  private String avatarUrl;

  /**
   * 用户名
   */
  private String userName;

  /**
   * 密码
   */
  private String password;

  /**
   * 邮箱
   */
  private String mail;

  /**
   * 角色(user普通用户,admin管理员)
   */
  private String role;

  /**
   * ip地址
   */
  private String ipAddress;


  /**
   * 创建时间
   */
  private Date createTime;

  /**
   * 修改时间
   */
  private Date updateTime;

  /**
   * 用户状态(0禁用,1正常)
   */
  private Integer status;

  /**
   * 0逻辑删除
   */
  @TableLogic
  private Integer isDelete;

  @TableField(exist = false)
  private static final long serialVersionUID = 1L;
}