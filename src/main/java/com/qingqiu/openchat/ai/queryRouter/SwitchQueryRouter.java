package com.qingqiu.openchat.ai.queryRouter;

import static dev.langchain4j.internal.ValidationUtils.ensureNotEmpty;

import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.rag.query.router.QueryRouter;
import jakarta.annotation.Resource;
import java.util.Collection;

/**
 * @author: QingQiu
 * @date: 2025/7/15 19:13
 * @description: 查询路由选择器
 */
public class SwitchQueryRouter implements QueryRouter {

  /**
   * contentRetrievers包含所有的contentRetriever
   */
  @Resource
  private Collection<ContentRetriever> contentRetrieverList;

  public SwitchQueryRouter() {
  }

  @Override
  public Collection<ContentRetriever> route(Query query) {
    /*return contentRetrievers.stream()
      .filter(contentRetriever -> !(contentRetriever instanceof WebSearchContentRetriever))
      .toList();*/
    return contentRetrieverList;
  }
}
