package com.qingqiu.openchat.service;

import com.qingqiu.openchat.domain.request.CreateModelProviderRequest;
import com.qingqiu.openchat.domain.request.QueryModelProviderRequest;
import com.qingqiu.openchat.domain.request.UpdateModelProviderRequest;
import com.qingqiu.openchat.domain.vo.ModelProviderVO;
import com.qingqiu.openchat.util.R;
import java.util.Collection;
import java.util.List;

/**
 * 厂商管理Service接口
 *
 * @author ageerle
 * @date 2025-12-14
 */
public interface ModelProviderService {

    List<ModelProviderVO> query(QueryModelProviderRequest queryModelProviderRequest);

    Boolean delete(Long id);

    Boolean update(UpdateModelProviderRequest updateModelProviderRequest);

    Boolean insert(CreateModelProviderRequest createModelProviderRequest);

}
