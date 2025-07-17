package com.qingqiu.aigeek.ai.rag;

import com.qingqiu.aigeek.ai.queryRouter.SwitchQueryRouter;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.DefaultRetrievalAugmentor.DefaultRetrievalAugmentorBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: QingQiu
 * @date: 2025/7/12 17:07
 * @description: 检索增强 扩增 配置类，RetrievalAugmentor是RAG的入口，可以对RAG进行增强
 *  DefaultQueryRouter会将每个查询路由到所有配置的 ContentRetriever
 *  embeddingModel 是一个模型，用于将文本转换为嵌入向量
 * embeddingStore 用于存储嵌入向量的组件
 */

@Configuration
public class RetrievalAugmentors {

  /**
   * 支不持联网搜索的
   * @return
   */
  @Bean
  @Qualifier("disableWebSearchQueryRouterRetrievalAugmentor")
  public DefaultRetrievalAugmentor disableWebSearchQueryRouterRetrievalAugmentor() {
    SwitchQueryRouter switchQueryRouter = new SwitchQueryRouter(false);
    //--检索query增强器,通过从不同的数据源查询检索，给chat返回增强后的对话信息
    return DefaultRetrievalAugmentor.builder()
        .queryRouter(switchQueryRouter)
        .build();
  }

  /**
   * 支持联网搜索的
   * @return
   */
  @Bean
  @Qualifier("enableWebSearchQueryRouterRetrievalAugmentor")
  public DefaultRetrievalAugmentor enableWebSearchQueryRouterRetrievalAugmentor() {
    SwitchQueryRouter switchQueryRouter = new SwitchQueryRouter(true);
    //--检索query增强器,通过从不同的数据源查询检索，给chat返回增强后的对话信息
    return DefaultRetrievalAugmentor.builder()
        .queryRouter(switchQueryRouter)
        .build();
  }

  /**
   * DefaultRetrievalAugmentorBuilder暴露出去，根据用户是否联网搜索传入switchQueryRouter,进而构建defaultQueryRouterRetrievalAugmentor
   * 暴漏builder优点是可以任意组合 Retriever。
   * 此时先不考虑该方案，选的方案是 直接暴露出去两个bean DefaultRetrievalAugmentor，一个支持联网搜索，一个不支持联网搜索
   * @return
   */
//  @Bean
  public DefaultRetrievalAugmentorBuilder defaultQueryRouterRetrievalAugmentorBuilder() {
    //--检索query增强器,通过从不同的数据源查询检索，给chat返回增强后的对话信息
    return DefaultRetrievalAugmentor.builder();
  }

}
