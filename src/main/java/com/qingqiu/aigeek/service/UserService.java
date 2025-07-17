package com.qingqiu.aigeek.service;

import com.qingqiu.aigeek.domain.dto.UserLoginDTO;
import com.qingqiu.aigeek.domain.dto.UserRegisterDTO;
import com.qingqiu.aigeek.domain.dto.UserRegisterMailDTO;
import com.qingqiu.aigeek.domain.vo.LoginUserVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author 懒大王Smile
 * @description 针对表【user(用户表)】的数据库操作Service
 * @createDate 2024-09-12 22:19:13
 */
public interface UserService  {

  LoginUserVO login(UserLoginDTO userLogin);

  LoginUserVO register(UserRegisterDTO userRegister);

  Boolean logout(HttpServletRequest httpServletRequest);

  LoginUserVO getLoginUser();

  Boolean sendRegisterCode(UserRegisterMailDTO userRegisterMail);

}
