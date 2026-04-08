package com.qingqiu.openchat.service.impl;

import com.qingqiu.openchat.domain.request.CreateModelProviderRequest;
import com.qingqiu.openchat.domain.request.QueryModelProviderRequest;
import com.qingqiu.openchat.domain.request.UpdateModelProviderRequest;
import com.qingqiu.openchat.domain.vo.ModelProviderVO;
import com.qingqiu.openchat.mapper.ModelProviderMapper;
import com.qingqiu.openchat.service.ModelProviderService;
import jakarta.annotation.Resource;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author: Qing Qiu
 * @date: 2026/4/8 20:38
 * @description:
 */
@Service
@Slf4j
public class ModelProviderServiceImpl implements ModelProviderService {

  @Resource
  private ModelProviderMapper modelProviderMapper;

  @Override
  public Boolean delete(Long id) {
    return modelProviderMapper.deleteById(id) > 0;
  }

  @Override
  public List<ModelProviderVO> query(QueryModelProviderRequest queryModelProviderRequest) {
    return List.of();
  }

  @Override
  public Boolean update(UpdateModelProviderRequest updateModelProviderRequest) {
    return null;
  }

  @Override
  public Boolean insert(CreateModelProviderRequest createModelProviderRequest) {
    return null;
  }
}
