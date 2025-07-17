package com.qingqiu.aigeek.util;


/**
 * 用户上下文，保存用户id
 */
public class UserContext {

  private static final ThreadLocal<Long> tl = new ThreadLocal<>();

  public static void saveUser(Long userId) {
    tl.set(userId);
  }

  public static Long getUser() {
    return tl.get();
  }

  public static void removeUser() {
    tl.remove();
  }
}
