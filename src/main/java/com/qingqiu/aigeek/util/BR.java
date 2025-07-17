package com.qingqiu.aigeek.util;

import com.qingqiu.aigeek.enums.BusinessExceptionEnum;
import java.io.Serializable;
import lombok.Data;

/**
 * 通用返回类 返回给前端数据
 * @author 懒大王Smile
 */
@Data
public class BR<T> implements Serializable {

  private int code;

  private T data;

  private String message;

  public BR(int code, T data, String message) {
    this.code = code;
    this.data = data;
    this.message = message;
  }

  public BR(int code, T data) {
    this(code, data, "");
  }

  public BR(BusinessExceptionEnum errorCode) {
    this(errorCode.getCode(), null, errorCode.getMessage());
  }
}
