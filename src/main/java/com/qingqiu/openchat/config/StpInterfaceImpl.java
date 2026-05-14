package com.qingqiu.openchat.config;

import cn.dev33.satoken.stp.StpInterface;
import com.mybatisflex.core.query.QueryWrapper;
import com.qingqiu.openchat.domain.entity.User;
import com.qingqiu.openchat.mapper.UserMapper;
import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Sa-Token 角色和权限加载器
 */
@Component
public class StpInterfaceImpl implements StpInterface {

  @Resource
  private UserMapper userMapper;

  @Override
  public List<String> getPermissionList(Object loginId, String loginType) {
    return Collections.emptyList();
  }

  @Override
  public List<String> getRoleList(Object loginId, String loginType) {
    if (loginId == null) {
      return Collections.emptyList();
    }
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq(User::getId, Long.valueOf(loginId.toString()));
    User user = userMapper.selectOneByQuery(queryWrapper);
    if (user == null || user.getRole() == null) {
      return Collections.emptyList();
    }
    return Collections.singletonList(user.getRole());
  }
}