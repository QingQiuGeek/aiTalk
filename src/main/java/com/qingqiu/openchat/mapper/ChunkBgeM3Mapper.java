package com.qingqiu.openchat.mapper;

import com.mybatisflex.core.BaseMapper;
import com.qingqiu.openchat.domain.entity.ChunkBgeM3;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @author charon
 * @description 针对表【chunk_bge_m3】的数据库操作Mapper
 * @createDate 2025-12-02 15:44:34
 * @Entity com.kama.jchatmind.model.entity.ChunkBgeM3
 */
public interface ChunkBgeM3Mapper extends BaseMapper<ChunkBgeM3> {

    ChunkBgeM3 selectById(String id);

    int deleteById(String id);

    int updateById(ChunkBgeM3 chunkBgeM3);

    List<ChunkBgeM3> similaritySearch(
            @Param("kbId") String kbId,
            @Param("vectorLiteral") String vectorLiteral,
            @Param("limit") int limit
    );
}
