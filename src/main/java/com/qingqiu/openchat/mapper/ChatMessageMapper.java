package com.qingqiu.openchat.mapper;

import com.mybatisflex.core.BaseMapper;
import com.qingqiu.openchat.domain.entity.ChatMessage;
import java.util.List;

/**
 * @author charon
 * @description 针对表【chat_message】的数据库操作Mapper
 * @createDate 2025-12-02 15:40:13
 * @Entity com.kama.jchatmind.model.entity.ChatMessage
 */
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

    ChatMessage selectById(String id);

    List<ChatMessage> selectBySessionId(String sessionId);

    List<ChatMessage> selectBySessionIdRecently(String sessionId, int limit);

    int deleteById(String id);

    int updateById(ChatMessage chatMessage);
}
