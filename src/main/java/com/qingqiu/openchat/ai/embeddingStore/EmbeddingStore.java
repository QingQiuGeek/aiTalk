package com.qingqiu.openchat.ai.embeddingStore;

import com.qingqiu.openchat.config.EmbeddingStoreConfig;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.DefaultMetadataStorageConfig;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: QingQiu
 * @date: 2025/7/11
 * @description: PgVectorEmbeddingStore配置类
 * TODO 确保文档可以加载进pgvector
 */

@Configuration
@Data
public class EmbeddingStore {

    @Resource
    EmbeddingStoreConfig embeddingStoreConfig;

    /**
     * 基于pgvector的向量存储
     * @return
     */
    @Bean
    @Qualifier("pgVectorEmbeddingStore")
    public PgVectorEmbeddingStore pgVectorEmbeddingStore() {
        return PgVectorEmbeddingStore
            .builder()
            .host(embeddingStoreConfig.getHost())
            .port(embeddingStoreConfig.getPort())
            .database(embeddingStoreConfig.getDatabase())
            .user(embeddingStoreConfig.getUsername())
            .password(embeddingStoreConfig.getPassword())
            .table(embeddingStoreConfig.getTable())
            .dimension(embeddingStoreConfig.getDimension())
            .metadataStorageConfig(DefaultMetadataStorageConfig.defaultConfig())
            .build();
    }

    /**
     * 基于内存的向量存储
     * @return
     */
    @Bean
    @Qualifier("inMemoryEmbeddingStore")
    public InMemoryEmbeddingStore<TextSegment> inMemoryEmbeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }
}
