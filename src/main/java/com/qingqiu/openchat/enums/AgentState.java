package com.qingqiu.openchat.enums;

/**
 * 代理执行状态的枚举类
 */
public enum AgentState {

    /**
     * 空闲状态
     */
    IDLE,

    /**
     * 运行中状态
     */
    RUNNING,

    /**
     * 计划中
     */
    PLANNING,

    /**
     * 思考中
     */
    THINKING,

    /**
     * 执行中
     */
    EXECUTING,

    /**
     * 已完成状态
     */
    FINISHED,

    /**
     * 错误状态
     */
    ERROR
}