package com.qingqiu.aigeek.util;

import static com.qingqiu.aigeek.constant.Common.EMAIL_REGEX;
import static com.qingqiu.aigeek.constant.Common.PASSWORD_REGEX;
import static com.qingqiu.aigeek.constant.Common.USERNAME_REGEX;

import java.util.regex.Pattern;

/**
 * @Author: QingQiu
 * @Date: 2025/6/28
 * @Description: 正则表达式校验工具类
 */

public class RegularUtil {

  //邮箱格式校验
  public static boolean checkMail(String mail) {
    return !Pattern.compile(EMAIL_REGEX).matcher(mail).matches();
  }

  //用户名格式校验
  public static boolean checkUserName(String userName) {
    return !Pattern.compile(USERNAME_REGEX).matcher(userName).matches();
  }

  //密码格式校验
  public static boolean checkPassword(String password) {
    return !Pattern.compile(PASSWORD_REGEX).matcher(password).matches();
  }

}
