package com.qingqiu.aigeek.exception;

import com.qingqiu.aigeek.enums.BusinessExceptionEnum;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author:懒大王Smile
 * @date: 2024/9/12 23:08
 * @description: 自定义线程池拒绝策略
 */

public class ExecutionRejectHandler implements RejectedExecutionHandler {

  @Override
  public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
    throw new BusinessException(BusinessExceptionEnum.SYSTEM_BUSY.getCode(), BusinessExceptionEnum.SYSTEM_BUSY.getMessage());
  }
}
