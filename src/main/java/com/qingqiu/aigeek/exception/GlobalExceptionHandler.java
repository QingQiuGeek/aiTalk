package com.qingqiu.aigeek.exception;

import com.qingqiu.aigeek.enums.BusinessExceptionEnum;
import com.qingqiu.aigeek.util.BR;
import com.qingqiu.aigeek.util.R;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author:懒大王Smile
 * @date: 2024/9/12 23:08
 * @description: 全局异常处理器
 */

@Hidden
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  //@ExceptionHandler用来处理controller级别的异常
  @ExceptionHandler(BusinessException.class)
  public BR businessExceptionHandler(BusinessException e) {
    log.error("BusinessException", e);
    return R.error(e.getErrorCode(), e.getMessage());
  }

  //出现未定义的异常，统一抛出自定义状态码1000
  @ExceptionHandler(RuntimeException.class)
  public BR runtimeExceptionHandler(RuntimeException e) {
    log.error("RuntimeException", e);
    return R.error(BusinessExceptionEnum.SYSTEM_ERROR.getCode(), e.getMessage());
  }
}
