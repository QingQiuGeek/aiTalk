package com.qingqiu.aigeek.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.DefaultMetadataStorageConfig;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: QingQiu
 * @date: 2025/7/11
 * @description: PgVectorEmbeddingStore配置类
 * TODO 确保文档可以加载进pgvector
 */

@ConfigurationProperties(prefix = "datasource.pg")
@Configuration
@Data
public class EmbeddingStoreConfig {

    private String host;
    private Integer port;
    private String username;
    private String password;
    private String database;
    private String table;
    private Integer dimension;

}
