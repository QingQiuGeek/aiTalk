package com.qingqiu.aigeek.controller;

import com.qingqiu.aigeek.domain.dto.UserLoginDTO;
import com.qingqiu.aigeek.domain.dto.UserRegisterDTO;
import com.qingqiu.aigeek.domain.dto.UserRegisterMailDTO;
import com.qingqiu.aigeek.domain.vo.LoginUserVO;
import com.qingqiu.aigeek.service.UserService;
import com.qingqiu.aigeek.util.BR;
import com.qingqiu.aigeek.util.R;
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
  public BR<LoginUserVO> login(@RequestBody UserLoginDTO userLogin) {
    return R.ok(userService.login(userLogin));
  }

  /**
   * 用户注册
   */
  @PostMapping("/register")
  public BR<LoginUserVO> register(@RequestBody UserRegisterDTO userRegister) {
    return R.ok(userService.register(userRegister));
  }

  /**
   * 退出登录
   * @return
   */
  @PostMapping("/logout")
  public BR<Boolean> logout(HttpServletRequest httpServletRequest) {
    return R.ok(userService.logout(httpServletRequest));
  }

  /**
   * 获取当前登录用户
   * @return
   */
  @GetMapping("/getLoginUser")
  public BR<LoginUserVO> getLoginUser() {
    return R.ok(userService.getLoginUser());
  }

  @PostMapping("/sendRegisterCode")
  public BR<Boolean> sendRegisterCode(
      @RequestBody UserRegisterMailDTO userRegisterMail) {
    return R.ok(userService.sendRegisterCode(userRegisterMail));
  }

}
