package com.qingqiu.openchat.controller;

import com.qingqiu.openchat.domain.dto.UserLoginDTO;
import com.qingqiu.openchat.domain.dto.UserRegisterDTO;
import com.qingqiu.openchat.domain.dto.UserRegisterMailDTO;
import com.qingqiu.openchat.domain.vo.LoginUserVO;
import com.qingqiu.openchat.service.UserService;
import com.qingqiu.openchat.util.R;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: QingQiu
 * @date: 2025/7/15 23:34
 * @description:
 */

@RestController
@RequestMapping("/user")
public class UserController {

  @Resource
  private UserService userService;

  /**
   * 用户登录
   * @return
   */
  @PostMapping("/login")
  public R<LoginUserVO> login(@RequestBody UserLoginDTO userLogin) {
    return R.ok(userService.login(userLogin));
  }

  /**
   * 用户注册
   */
  @PostMapping("/register")
  public R<LoginUserVO> register(@RequestBody UserRegisterDTO userRegister) {
    return R.ok(userService.register(userRegister));
  }

  /**
   * 退出登录
   * @return
   */
  @PostMapping("/logout")
  public R<Boolean> logout(HttpServletRequest httpServletRequest) {
    return R.ok(userService.logout(httpServletRequest));
  }

  /**
   * 获取当前登录用户
   * @return
   */
  @GetMapping
  public R<LoginUserVO> getLoginUser() {
    return R.ok(userService.getLoginUser());
  }

  @PostMapping("/register-code")
  public R<Boolean> sendRegisterCode(@RequestBody UserRegisterMailDTO userRegisterMail) {
    return R.ok(userService.sendRegisterCode(userRegisterMail));
  }

}
