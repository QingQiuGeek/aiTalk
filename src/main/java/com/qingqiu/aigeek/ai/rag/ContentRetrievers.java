package com.qingqiu.aigeek.ai.rag;

import static com.qingqiu.aigeek.util.SearXNGUtil.getSearXNGParams;

import com.qingqiu.aigeek.config.RagConfig;
import com.qingqiu.aigeek.config.SearXNGConfig;
import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.community.web.search.searxng.SearXNGWebSearchEngine;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import dev.langchain4j.web.search.WebSearchEngine;
import jakarta.annotation.Resource;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: QingQiu
 * @date: 2025/7/11
 * @description: 检索增强（RAG）配置类
 *  embeddingModel 是一个模型，用于将文本转换为嵌入向量
 *  embeddingStore 用于存储嵌入向量的组件
 *  注意！！！RAG检索的embeddingStore和文档加载的embeddingStore要配置相同
 *  TODO 确保每个ContentRetriever都能实现
 */
@Configuration
@Data
public class ContentRetrievers {

  @Resource
  SearXNGConfig searXNGConfig;

  @Resource
  RagConfig ragConfig;

  @Resource
  private EmbeddingModel embeddingModel;

  @Autowired
  @Qualifier("pgVectorEmbeddingStore")
  PgVectorEmbeddingStore pgVectorEmbeddingStore;

  @Autowired
  @Qualifier("inMemoryEmbeddingStore")
  InMemoryEmbeddingStore<TextSegment> inMemoryEmbeddingStore;

  @Resource
  DocumentParser apacheTikaDocumentParser;

  @Resource
  DocumentSplitter documentSplitter;

  /*
  * 基于pgVectorEmbeddingStore的 rag
  * */
  @Bean
  @Qualifier("pgVectorContentRetriever")
  public EmbeddingStoreContentRetriever pgVectorContentRetriever() {
    //  自定义内容查询器 ！！！注意，数据预处理和查询处理的embeddingStore要相同
    return EmbeddingStoreContentRetriever.builder()
        .embeddingStore(pgVectorEmbeddingStore)
        .embeddingModel(embeddingModel)
        .maxResults(ragConfig.getMaxResults())
        .minScore(ragConfig.getMinScore())
        .build();
  }

  /*
  *  基于内存的 rag
  * */
  @Bean
  @Qualifier("inMemoryContentRetriever")
  public EmbeddingStoreContentRetriever inMemoryContentRetriever() {
    //  自定义内容查询器 ！！！注意，数据预处理和查询处理的embeddingStore要相同
    return EmbeddingStoreContentRetriever.builder()
        .embeddingStore(inMemoryEmbeddingStore)
        .embeddingModel(embeddingModel)
        .maxResults(ragConfig.getMaxResults())
        .minScore(ragConfig.getMinScore())
        .build();
  }

  /*
  * 基于联网搜索的 RAG
  * */
  @Bean
  public WebSearchContentRetriever webSearchContentRetriever(){
    //指定启用和禁用的搜索引擎
    Map<String, Object> searXNGParams = getSearXNGParams();
    //--联网搜索引擎,这里使用自建的searxng，官方文档：github.com/searxng/searxng
    WebSearchEngine webSearchEngine =
        SearXNGWebSearchEngine.builder().optionalParams(searXNGParams)
            .duration(Duration.ofSeconds(searXNGConfig.getTimeout()))
            .logRequests(true).logResponses(true).baseUrl(searXNGConfig.getUrl()).build();
    return WebSearchContentRetriever.builder()
        .webSearchEngine(webSearchEngine)
        .maxResults(ragConfig.getMaxResults())
        .build();
  }
}
