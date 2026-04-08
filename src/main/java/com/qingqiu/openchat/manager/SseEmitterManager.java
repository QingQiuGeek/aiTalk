package com.qingqiu.openchat.manager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.qingqiu.openchat.domain.dto.SseEventDTO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 管理 Server-Sent Events (SSE) 连接
 *
 * @author 懒大王Smile
 */
@Slf4j
@Component
public class SseEmitterManager {

    private final static Map<Long, Map<String, SseEmitter>> USER_SESSION_EMITTERS = new ConcurrentHashMap<>();

    public SseEmitterManager(ScheduledExecutorService scheduledExecutorService) {
        // 定时执行 SSE 心跳检测，第一次执行前先等 60 秒，之后每次执行间隔 60 秒
        scheduledExecutorService.scheduleWithFixedDelay(this::sseMonitor, 60L, 60L,
            TimeUnit.SECONDS);
    }

    /**
     * 建立与指定用户的 SSE 连接
     *
     * @param userId    用户的唯一标识符，用于区分不同用户的连接
     * @param sessionId 用户的唯一令牌，用于识别具体的连接
     * @return 返回一个 SseEmitter 实例，客户端可以通过该实例接收 SSE 事件
     */
    public SseEmitter connect(Long userId, String sessionId) {
        // 从 USER_SESSION_EMITTERS 中获取或创建当前用户的 SseEmitter 映射表（ConcurrentHashMap）
        // 每个用户可以有多个 SSE 连接，通过 sessionId 进行区分
        Map<String, SseEmitter> emitters = USER_SESSION_EMITTERS.computeIfAbsent(userId,
            k -> new ConcurrentHashMap<>());

        // 关闭已存在的SseEmitter
        SseEmitter oldEmitter = emitters.remove(sessionId);
        if (oldEmitter != null) {
            oldEmitter.complete();
        }

        // 1h 自动断开，发消息时间不会刷新
        SseEmitter emitter = new SseEmitter(1000L * 60 * 60L);
        emitters.put(sessionId, emitter);

        // 当 emitter 完成、超时或发生错误时，从映射表中移除对应的 sessionId
        emitter.onCompletion(() -> {
            SseEmitter remove = emitters.remove(sessionId);
            if (remove != null) {
                remove.complete();
            }
        });
        emitter.onTimeout(() -> {
            SseEmitter remove = emitters.remove(sessionId);
            if (remove != null) {
                remove.complete();
            }
        });
        emitter.onError((e) -> {
            SseEmitter remove = emitters.remove(sessionId);
            if (remove != null) {
                remove.complete();
            }
        });

        try {
            // 向客户端发送一条连接成功的事件
            emitter.send(SseEmitter.event().name("init-connect")
                .data("init-connect").comment("init-connect"));
        } catch (IOException e) {
            // 如果发送消息失败，则从映射表中移除 emitter
            emitters.remove(sessionId);
        }
        return emitter;
    }

    /**
     * 断开指定用户的 SSE 连接
     *
     * @param userId    用户的唯一标识符，用于区分不同用户的连接
     * @param sessionId 用户的唯一令牌，用于识别具体的连接
     */
    public void disconnect(Long userId, String sessionId) {
        if (userId == null || sessionId == null) {
            return;
        }
        Map<String, SseEmitter> emitters = USER_SESSION_EMITTERS.get(userId);
        if (MapUtil.isNotEmpty(emitters)) {
            try {
                SseEmitter sseEmitter = emitters.get(sessionId);
                sseEmitter.send(SseEmitter.event().name("disconnected")
                    .data("disconnected").comment("disconnected"));
                sseEmitter.complete();
            } catch (Exception exception) {
                log.error(exception.getMessage());
            }
            emitters.remove(sessionId);
        } else {
            USER_SESSION_EMITTERS.remove(userId);
        }
    }

    /**
     * SSE 心跳检测，关闭无效连接
     */
    public void sseMonitor() {
        final SseEmitter.SseEventBuilder heartbeat = SseEmitter.event().comment("heartbeat");
        // 记录需要移除的用户ID
        List<Long> toRemoveUsers = new ArrayList<>();

        USER_SESSION_EMITTERS.forEach((userId, emitterMap) -> {
            if (CollUtil.isEmpty(emitterMap)) {
                toRemoveUsers.add(userId);
                return;
            }

            emitterMap.entrySet().removeIf(entry -> {
                try {
                    entry.getValue().send(heartbeat);
                    return false;
                } catch (Exception ex) {
                    try {
                        entry.getValue().complete();
                    } catch (Exception ignore) {
                        // 忽略重复关闭异常
                    }
                    return true; // 发送失败 → 移除该连接
                }
            });

            // 移除空连接用户
            if (emitterMap.isEmpty()) {
                toRemoveUsers.add(userId);
            }
        });

        // 循环结束后统一清理空用户，避免并发修改异常
        toRemoveUsers.forEach(USER_SESSION_EMITTERS::remove);
    }

    /**
     * 向指定的用户会话发送消息
     *
     * @param userId  要发送消息的用户id
     * @param message 要发送的消息内容
     */
    public void sendMessage(Long userId, String message) {
        Map<String, SseEmitter> emitters = USER_SESSION_EMITTERS.get(userId);
        if (MapUtil.isNotEmpty(emitters)) {
            for (Map.Entry<String, SseEmitter> entry : emitters.entrySet()) {
                try {
                    // 格式化为标准SSE JSON格式
                    SseEventDTO eventDto = SseEventDTO.content(message);
                    entry.getValue().send(SseEmitter.event()
                        .name("message")
                        .data(JSONUtil.toJsonStr(eventDto)));
                } catch (Exception e) {
                    SseEmitter remove = emitters.remove(entry.getKey());
                    if (remove != null) {
                        remove.complete();
                    }
                }
            }
        } else {
            USER_SESSION_EMITTERS.remove(userId);
        }
    }

    /**
     * 本机全用户会话发送消息
     *
     * @param message 要发送的消息内容
     */
    public void sendMessage(String message) {
        for (Long userId : USER_SESSION_EMITTERS.keySet()) {
            sendMessage(userId, message);
        }
    }


    /**
     * 向指定的用户会话发送结构化事件
     *
     * @param userId   要发送消息的用户id
     * @param eventDto SSE事件对象
     */
    public void sendEvent(Long userId, SseEventDTO eventDto) {
        Map<String, SseEmitter> emitters = USER_SESSION_EMITTERS.get(userId);
        if (MapUtil.isNotEmpty(emitters)) {
            for (Map.Entry<String, SseEmitter> entry : emitters.entrySet()) {
                try {
                    entry.getValue().send(SseEmitter.event()
                        .name(eventDto.getEvent())
                        .data(JSONUtil.toJsonStr(eventDto)));
                } catch (Exception e) {
                    SseEmitter remove = emitters.remove(entry.getKey());
                    if (remove != null) {
                        remove.complete();
                    }
                }
            }
        } else {
            USER_SESSION_EMITTERS.remove(userId);
        }
    }


}
