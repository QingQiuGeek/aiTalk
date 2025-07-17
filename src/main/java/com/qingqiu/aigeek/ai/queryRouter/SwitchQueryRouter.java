package com.qingqiu.aigeek.ai.queryRouter;

import static dev.langchain4j.internal.ValidationUtils.ensureNotEmpty;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableCollection;

import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;
import dev.langchain4j.rag.query.Metadata;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.rag.query.router.LanguageModelQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author: QingQiu
 * @date: 2025/7/15 19:13
 * @description: 查询路由选择器
 */
public class SwitchQueryRouter implements QueryRouter {

  /**
   * contentRetrievers包含定义的所有的contentRetriever
   */
  @Resource
  private Collection<ContentRetriever> contentRetrievers;

  private final Boolean enableSearch;

  public SwitchQueryRouter(Boolean enableSearch) {
    this.enableSearch = enableSearch;
  }

  @Override
  public Collection<ContentRetriever> route(Query query) {
    //用户的文本，目前只是根据enableSearch实现了简单的路由规则，要实现更复杂的，可以根据用户的文本内容筛选过滤contentRetriever，如下：
//    1.
//    String text = query.text();
//    query.metadata().chatMemoryId()
//    String role   = userService.roleOf(userId); // 权限逻辑路由规则
//    return List.of(retrievers.get(role));
//    2.
//    如果包含“最新”或“今天”，启用 Web 搜索
//    boolean needsWebSearch = text.contains("最新") || text.contains("今天");
//    3.
//    LanguageModelQueryRouter router = LanguageModelQueryRouter.builder().
//        .chatModel()
//        .fallbackStrategy()
//        .retrieverToDescription(List.of(Map.of("name1",contentRetriever1,"name2",contentRetriever2)))
//        .build()

    if (!enableSearch) {
      //开关关闭，不走联网检索
      return contentRetrievers.stream().filter(contentRetriever -> !(contentRetriever instanceof WebSearchContentRetriever)).toList();
    }
    return contentRetrievers;
  }
}
