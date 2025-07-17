package com.qingqiu.aigeek.config;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author: QingQiu
 * @date: 2025/7/12 16:30
 * @description: searXNG联网搜索相关的配置类
 */
@ConfigurationProperties(prefix = "searxng")
@Configuration
@Data
public class SearXNGConfig {

  private String url;

  private Integer timeout;

}
