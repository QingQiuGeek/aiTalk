package com.qingqiu.aigeek.exception;

import lombok.Getter;

/**
 * @author:懒大王Smile
 * @date: 2024/9/12 23:08
 * @description: 自定义异常类
 */
//spring默认回滚运行时异常和Error  https://blog.csdn.net/hanjiaqian/article/details/120501741
@Getter
public class BusinessException extends RuntimeException {

  Integer errorCode;

  public BusinessException(Integer errorCode, String errorInfo) {
    super(errorInfo);
    this.errorCode = errorCode;
  }

}
