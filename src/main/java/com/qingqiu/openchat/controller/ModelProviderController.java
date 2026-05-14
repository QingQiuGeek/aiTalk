package com.qingqiu.openchat.controller;

import com.qingqiu.openchat.domain.request.CreateModelProviderRequest;
import com.qingqiu.openchat.domain.request.QueryModelProviderRequest;
import com.qingqiu.openchat.domain.request.UpdateModelProviderRequest;
import com.qingqiu.openchat.domain.vo.ModelProviderVO;
import com.qingqiu.openchat.service.ModelProviderService;
import com.qingqiu.openchat.util.R;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 厂商管理
 *
 * @author ageerle
 * @date 2025-12-14
 */
@Validated
@RestController
@RequestMapping("/model/provider")
public class ModelProviderController {

    @Resource
    private ModelProviderService modelProviderService;

    /**
     * 查询厂商管理列表
     */
    @GetMapping
    public R<List<ModelProviderVO>> list(QueryModelProviderRequest queryModelProviderRequest) {
        return R.ok(modelProviderService.query(queryModelProviderRequest));
    }

    /**
     * 新增厂商管理
     */
    @PostMapping
    public R<Boolean> add(@RequestBody CreateModelProviderRequest createModelProviderRequest) {
        return R.ok(modelProviderService.insert(createModelProviderRequest));
    }

    /**
     * 修改厂商管理
     */
    @PutMapping
    public R<Boolean> update(@RequestBody UpdateModelProviderRequest updateModelProviderRequest) {
        return R.ok(modelProviderService.update(updateModelProviderRequest));
    }

    /**
     * 删除厂商管理
     *
     * @param id 主键串
     */
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(modelProviderService.delete(id));
    }
}
