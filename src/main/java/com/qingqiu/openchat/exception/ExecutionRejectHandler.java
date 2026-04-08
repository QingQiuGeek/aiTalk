package com.qingqiu.openchat.exception;

import com.qingqiu.openchat.enums.BizExceptionEnum;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: 懒大王Smile
 * @date: 2024/9/12 23:08
 * @description: 自定义线程池拒绝策略
 */

public class ExecutionRejectHandler implements RejectedExecutionHandler {

  @Override
  public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
    throw new BizException(BizExceptionEnum.SYSTEM_BUSY.getCode(), BizExceptionEnum.SYSTEM_BUSY.getMessage());
  }
}
