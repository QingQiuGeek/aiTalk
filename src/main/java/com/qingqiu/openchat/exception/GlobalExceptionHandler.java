package com.qingqiu.openchat.exception;

import com.qingqiu.openchat.enums.BizExceptionEnum;
import com.qingqiu.openchat.util.R;
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
  @ExceptionHandler(BizException.class)
  public R businessExceptionHandler(BizException e) {
    log.error("BusinessException", e);
    return R.error(e.getErrorCode(), e.getMessage());
  }

  //出现未定义的异常，统一抛出自定义状态码1000
  @ExceptionHandler(RuntimeException.class)
  public R runtimeExceptionHandler(RuntimeException e) {
    log.error("RuntimeException", e);
    return R.error(BizExceptionEnum.SYSTEM_ERROR.getCode(), e.getMessage());
  }
}
