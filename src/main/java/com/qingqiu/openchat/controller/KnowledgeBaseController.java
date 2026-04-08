package com.qingqiu.openchat.controller;

import com.qingqiu.openchat.domain.request.CreateKnowledgeBaseRequest;
import com.qingqiu.openchat.domain.request.UpdateKnowledgeBaseRequest;
import com.qingqiu.openchat.domain.vo.KnowledgeBaseVO;
import com.qingqiu.openchat.service.KnowledgeBaseService;
import com.qingqiu.openchat.util.R;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/knowledge-bases")
public class KnowledgeBaseController {

    @Resource
    private KnowledgeBaseService knowledgeBaseService;

    // 查询所有知识库
    @GetMapping
    public R<List<KnowledgeBaseVO>> getKnowledgeBases() {
        return R.ok(knowledgeBaseService.getKnowledgeBases());
    }

    // 创建知识库
    @PostMapping
    public R<String> createKnowledgeBase(@RequestBody CreateKnowledgeBaseRequest request) {
        return R.ok(knowledgeBaseService.createKnowledgeBase(request));
    }

    // 删除知识库
    @DeleteMapping("/{knowledgeBaseId}")
    public R<Boolean> deleteKnowledgeBase(@PathVariable String knowledgeBaseId) {
        return R.ok(knowledgeBaseService.deleteKnowledgeBase(knowledgeBaseId));
    }

    // 更新知识库的名称、描述，不涉及文档或文件
    @PutMapping
    public R<Boolean> updateKnowledgeBase(@RequestBody UpdateKnowledgeBaseRequest request) {
        return R.ok(knowledgeBaseService.updateKnowledgeBase( request));
    }
}
