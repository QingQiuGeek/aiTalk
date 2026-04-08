package com.qingqiu.openchat.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mybatisflex.core.query.QueryWrapper;
import com.qingqiu.openchat.convert.KnowledgeBaseConverter;
import com.qingqiu.openchat.exception.BizException;
import com.qingqiu.openchat.mapper.KnowledgeBaseMapper;
import com.qingqiu.openchat.domain.dto.KnowledgeBaseDTO;
import com.qingqiu.openchat.domain.entity.KnowledgeBase;
import com.qingqiu.openchat.domain.request.CreateKnowledgeBaseRequest;
import com.qingqiu.openchat.domain.request.UpdateKnowledgeBaseRequest;
import com.qingqiu.openchat.domain.vo.KnowledgeBaseVO;
import com.qingqiu.openchat.service.KnowledgeBaseService;
import com.qingqiu.openchat.util.UserContext;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    @Resource
    private KnowledgeBaseMapper knowledgeBaseMapper;
    @Resource
    private  KnowledgeBaseConverter knowledgeBaseConverter;

    @Override
    public List<KnowledgeBaseVO> getKnowledgeBases() {
        QueryWrapper wrapper = QueryWrapper.create().eq(KnowledgeBase::getId, UserContext.getUser());
        List<KnowledgeBase> knowledgeBases = knowledgeBaseMapper.selectListByQuery(wrapper);
        return knowledgeBases.stream().map(KnowledgeBase::convertToVO).toList();
    }

    @Override
    public String createKnowledgeBase(CreateKnowledgeBaseRequest request) {
        try {
            // 将 CreateKnowledgeBaseRequest 转换为 KnowledgeBaseDTO
            KnowledgeBaseDTO knowledgeBaseDTO = knowledgeBaseConverter.toDTO(request);
            
            // 将 KnowledgeBaseDTO 转换为 KnowledgeBase 实体
            KnowledgeBase knowledgeBase = knowledgeBaseConverter.toEntity(knowledgeBaseDTO);
            
            // 设置创建时间和更新时间
            LocalDateTime now = LocalDateTime.now();
            knowledgeBase.setCreatedAt(now);
            knowledgeBase.setUpdatedAt(now);
            
            // 插入数据库，ID 由数据库自动生成
            int result = knowledgeBaseMapper.insert(knowledgeBase);
            if (result <= 0) {
                throw new BizException("创建知识库失败");
            }
            
            return knowledgeBase.getId();
        } catch (JsonProcessingException e) {
            throw new BizException("创建知识库时发生序列化错误: " + e.getMessage());
        }
    }

    @Override
    public Boolean deleteKnowledgeBase(String knowledgeBaseId) {
        KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(knowledgeBaseId);
        if (knowledgeBase == null) {
            throw new BizException("知识库不存在: " + knowledgeBaseId);
        }
        
        int result = knowledgeBaseMapper.deleteById(knowledgeBaseId);
        if (result <= 0) {
            throw new BizException("删除知识库失败");
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean updateKnowledgeBase(UpdateKnowledgeBaseRequest request) {
        String knowledgeBaseId = request.getKnowledgeBaseId();
        if(StringUtils.isBlank(knowledgeBaseId)){
            throw new BizException(500, "知识库ID不能为空");
        }
        try {
            // 查询现有的知识库
            KnowledgeBase existingKnowledgeBase = knowledgeBaseMapper.selectById(knowledgeBaseId);
            if (existingKnowledgeBase == null) {
                throw new BizException("知识库不存在: " + knowledgeBaseId);
            }
            
            // 将现有 KnowledgeBase 转换为 KnowledgeBaseDTO
            KnowledgeBaseDTO knowledgeBaseDTO = knowledgeBaseConverter.toDTO(existingKnowledgeBase);
            
            // 使用 UpdateKnowledgeBaseRequest 更新 KnowledgeBaseDTO
            knowledgeBaseConverter.updateDTOFromRequest(knowledgeBaseDTO, request);
            
            // 将更新后的 KnowledgeBaseDTO 转换回 KnowledgeBase 实体
            KnowledgeBase updatedKnowledgeBase = knowledgeBaseConverter.toEntity(knowledgeBaseDTO);
            
            // 保留原有的 ID 和创建时间
            updatedKnowledgeBase.setId(existingKnowledgeBase.getId());
            updatedKnowledgeBase.setCreatedAt(existingKnowledgeBase.getCreatedAt());
            updatedKnowledgeBase.setUpdatedAt(LocalDateTime.now());
            
            // 更新数据库
            int result = knowledgeBaseMapper.updateById(updatedKnowledgeBase);
            if (result <= 0) {
                throw new BizException("更新知识库失败");
            }
        } catch (JsonProcessingException e) {
            throw new BizException("更新知识库时发生序列化错误: " + e.getMessage());
        }
        return Boolean.TRUE;
    }
}
