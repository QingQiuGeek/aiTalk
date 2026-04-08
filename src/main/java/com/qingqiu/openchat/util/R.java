package com.qingqiu.openchat.util;


import com.qingqiu.openchat.enums.BizExceptionEnum;

/**
 * 返回工具类
 * @author 懒大王Smile
 */
public class R<T> {

  private int code;

  private T data;

  private String message;

  public R(int code, T data, String message) {
    this.code = code;
    this.data = data;
    this.message = message;
  }

  public R(int code, T data) {
    this(code, data, "");
  }

  public R(BizExceptionEnum errorCode) {
    this(errorCode.getCode(), null, errorCode.getMessage());
  }

  /**
   * 成功
   * @param data
   * @param <T>
   * @return
   */
  public static <T> R<T> ok(T data) {
    return new R<>(200, data, "ok");
  }

  /**
   * 失败
   *
   * @param errorCode
   * @return
   */
  public static R error(BizExceptionEnum errorCode) {
    return new R<>(errorCode);
  }

  /**
   * 失败
   *
   * @param errorCode
   * @return
   */
  public static R error(Integer errorCode, String message) {
    return new R(errorCode, null, message);
  }
}
