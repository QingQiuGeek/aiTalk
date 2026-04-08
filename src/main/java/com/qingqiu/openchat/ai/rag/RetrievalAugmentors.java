package com.qingqiu.openchat.ai.rag;

import com.qingqiu.openchat.ai.queryRouter.SwitchQueryRouter;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.DefaultRetrievalAugmentor.DefaultRetrievalAugmentorBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: QingQiu
 * @date: 2025/7/12 17:07
 * @description: 检索增强 扩增 配置类，RetrievalAugmentor是RAG的入口，可以对RAG进行增强
 *  DefaultQueryRouter 会将每个查询路由到所有配置的 ContentRetriever
 */

@Configuration
public class RetrievalAugmentors {

  /**
   * @return
   */
  @Bean
  @Qualifier("disableWebSearchQueryRouterRetrievalAugmentor")
  public DefaultRetrievalAugmentor disableWebSearchQueryRouterRetrievalAugmentor() {
    //--检索query增强器,通过从不同的数据源查询检索，给chat返回增强后的对话信息
    return DefaultRetrievalAugmentor.builder()
        .queryRouter(new SwitchQueryRouter())
        .build();
  }

}
