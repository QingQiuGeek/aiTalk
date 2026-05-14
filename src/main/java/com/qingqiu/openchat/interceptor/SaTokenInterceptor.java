package com.qingqiu.openchat.interceptor;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import com.qingqiu.openchat.enums.BizExceptionEnum;
import com.qingqiu.openchat.exception.BizException;
import com.qingqiu.openchat.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @Author: QingQiu
 * @Date: 2025/6/28
 * @Description: 基于sa-token实现的拦截器，实现了token刷新、登录校验
 */
@Slf4j
@Component
public class SaTokenInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (requestAttributes == null) {
      throw new BizException(BizExceptionEnum.REQUEST_ERROR.getCode(), BizExceptionEnum.REQUEST_ERROR.getMessage());
    }
    String requestURI = request.getRequestURI();
    if (requestURI.contains("/ai")) {
      return true;
    }

    try {
      StpUtil.checkLogin();
      if (requestURI.contains("/admin")) {
        StpUtil.checkRole("admin");
      }
      Long userId = Long.valueOf(StpUtil.getLoginId().toString());
      UserContext.saveUser(userId);
      return true;
    } catch (NotLoginException e) {
      throw e;
    }
  }

  // 移除用户,防止内存泄漏!!!
  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) {
    UserContext.removeUser();
  }

}
