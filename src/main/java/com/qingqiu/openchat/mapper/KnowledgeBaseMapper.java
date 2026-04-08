package com.qingqiu.openchat.mapper;

import com.mybatisflex.core.BaseMapper;
import com.qingqiu.openchat.domain.entity.KnowledgeBase;
import java.util.List;

/**
* @author charon
* @description 针对表【knowledge_base】的数据库操作Mapper
* @createDate 2025-12-02 15:42:24
* @Entity com.kama.jchatmind.model.entity.KnowledgeBase
*/
public interface KnowledgeBaseMapper extends BaseMapper<KnowledgeBase> {

    KnowledgeBase selectById(String id);

    List<KnowledgeBase> selectByIdBatch(List<String> ids);

    int deleteById(String id);

    int updateById(KnowledgeBase knowledgeBase);
}
