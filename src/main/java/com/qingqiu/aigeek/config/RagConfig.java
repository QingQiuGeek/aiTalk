package com.qingqiu.aigeek.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author: QingQiu
 * @date: 2025/7/12 16:30
 * @description: rag相关的配置类
 */
@ConfigurationProperties(prefix = "content-retriever")
@Configuration
@Data
public class RagConfig {

  private Integer maxResults;

  private double minScore;

}
