package com.qingqiu.openchat.exception;

import lombok.Getter;

/**
 * @author:懒大王Smile
 * @date: 2024/9/12 23:08
 * @description: 自定义异常类
 */
//spring默认回滚运行时异常和Error  https://blog.csdn.net/hanjiaqian/article/details/120501741
@Getter
public class BizException extends RuntimeException {

  Integer errorCode;

  public BizException(Integer errorCode, String errorInfo) {
    super(errorInfo);
    this.errorCode = errorCode;
  }

  public BizException(String errorInfo) {
    super(errorInfo);
  }

}
