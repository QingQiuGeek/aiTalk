package com.qingqiu.openchat.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
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
@Table(value = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {

  /**
   * 用户ID
   */
  @Id(keyType = KeyType.Auto)
  private Long userId;

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
   * 创建时间
   */
  private Date createAt;

  /**
   * 修改时间
   */
  private Date updateAt;

  /**
   * 用户状态(0禁用,1正常)
   */
  private Integer status;

  /**
   * 0逻辑删除
   */
  @Column(isLogicDelete = true)
  private Integer isDelete;

  private static final long serialVersionUID = 1L;
}