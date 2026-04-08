package com.qingqiu.openchat.service;

import com.qingqiu.openchat.domain.request.CreateKnowledgeBaseRequest;
import com.qingqiu.openchat.domain.request.UpdateKnowledgeBaseRequest;
import com.qingqiu.openchat.domain.vo.KnowledgeBaseVO;
import java.util.List;

public interface KnowledgeBaseService {
    List<KnowledgeBaseVO> getKnowledgeBases();

    String createKnowledgeBase(CreateKnowledgeBaseRequest request);

    Boolean deleteKnowledgeBase(String knowledgeBaseId);

    Boolean updateKnowledgeBase(UpdateKnowledgeBaseRequest request);
}

