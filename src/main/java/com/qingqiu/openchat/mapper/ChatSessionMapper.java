package com.qingqiu.openchat.mapper;

import com.mybatisflex.core.BaseMapper;
import com.qingqiu.openchat.domain.entity.ChatSession;
import java.util.List;

/**
 * @author charon
 * @description 针对表【chat_session】的数据库操作Mapper
 * @createDate 2025-12-02 14:52:46
 * @Entity com.kama.jchatmind.model.entity.ChatSession
 */
public interface ChatSessionMapper extends BaseMapper<ChatSession> {

    ChatSession selectById(String id);

    List<ChatSession> selectByAgentId(Long agentId);

    int deleteById(String id);

    int updateById(ChatSession chatSession);
}
