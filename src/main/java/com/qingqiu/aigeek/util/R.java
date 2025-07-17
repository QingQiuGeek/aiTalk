package com.qingqiu.aigeek.util;


import com.qingqiu.aigeek.enums.BusinessExceptionEnum;

/**
 * 返回工具类
 * @author 懒大王Smile
 */
public class R {

  /**
   * 成功
   * @param data
   * @param <T>
   * @return
   */
  public static <T> BR<T> ok(T data) {
    return new BR<>(200, data, "ok");
  }

  /**
   * 失败
   *
   * @param errorCode
   * @return
   */
  public static BR error(BusinessExceptionEnum errorCode) {
    return new BR<>(errorCode);
  }

  /**
   * 失败
   *
   * @param errorCode
   * @return
   */
  public static BR error(Integer errorCode, String message) {
    return new BR(errorCode, null, message);
  }
}
