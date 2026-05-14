package com.qingqiu.openchat.mapper;

import com.mybatisflex.core.BaseMapper;
import com.qingqiu.openchat.domain.entity.Document;
import java.util.List;

/**
 * @author charon
 * @description 针对表【document】的数据库操作Mapper
 * @createDate 2025-12-02 15:42:18
 * @Entity com.kama.jchatmind.model.entity.Document
 */
public interface DocumentMapper extends BaseMapper<Document> {
    Document selectById(Long id);

    List<Document> selectByKbId(Long kbId);

    int deleteById(Long id);

    int updateById(Document document);
}
