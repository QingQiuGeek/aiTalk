package com.qingqiu.aigeek.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.qingqiu.aigeek.config.SearXNGConfig;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * @author: QingQiu
 * @date: 2025/7/12 16:12
 * @description:
 */
@Slf4j
@Data
public class SearXNGUtil {

  @Resource
  SearXNGConfig searXNGConfig;

  private static String url;

  private static Integer timeout;

  SearXNGUtil() {
    url = searXNGConfig.getUrl();
    timeout = searXNGConfig.getTimeout();
  }

  public static List<SearchXNGResult> search(String query, String host, Integer port) {
    // 构造请求 URL
    Map<String, Object> searXNGParams = getSearXNGParams();
    searXNGParams.put("q", query);
    List<SearchXNGResult> results = new ArrayList<>();
    try {
      String response = HttpUtil.get(url, searXNGParams, timeout);
      // 取出返回结果的前 5 条
      JSONObject jsonObject = JSONUtil.parseObj(response);
      // 提取 organic_results 部分
      JSONArray organicResults = jsonObject.getJSONArray("organic_results");
      List<Object> objects = organicResults.subList(0, 6);
      // 拼接搜索结果为字符串
      String parseResult = objects.stream().map(obj -> {
        JSONObject tmpJSONObject = (JSONObject) obj;
        return tmpJSONObject.toString();
      }).collect(Collectors.joining(","));
      //TODO 搜索结果解析，需要部署searXNG
    } catch (Exception e) {
      log.error("Error WebSearching: {}" , e.getMessage());
    }
    return results;
  }

  public static Map<String,Object> getSearXNGParams(){
    Map<String,Object> params = new HashMap<>();
    params.put("categories","general");
    params.put("disabled_engines","arch linux wiki__it,artic__images,arxiv__science,bandcamp__music,wikipedia__general,bing images__images,bing news__news,bing videos__videos,openverse__images,chefkoch__other,currency__general,deviantart__images,docker hub__it,wikidata__general,duckduckgo__general,etymonline__other,flickr__images,genius__music,gentoo__it,github__it,google__general,google images__images,google news__news,google videos__videos,google scholar__science,hoogle__it,kickass__files,lemmy communities__social media,lemmy users__social media,lemmy posts__social media,lemmy comments__social media,z-library__files,library of congress__images,lingva__general,mastodon users__social media,mastodon hashtags__social media,mdn__it,mixcloud__music,mankier__it,openairedatasets__science,openairepublications__science,openstreetmap__map,pdbe__science,photon__map,pinterest__images,piped__videos,piped.music__music,piratebay__files,podcastindex__other,public domain image archive__images,pubmed__science,pypi__it,qwant__general,qwant news__news,qwant images__images,qwant videos__videos,radio browser__music,sepiasearch__videos,soundcloud__music,stackoverflow__it,askubuntu__it,superuser__it,startpage__general,startpage news__news,startpage images__images,solidtorrents__files,unsplash__images,yahoo news__news,youtube__videos,youtube__music,dailymotion__videos,vimeo__videos,wikinews__news,wiktionary__other,wikicommons.images__images,wikicommons.videos__videos,wikicommons.audio__music,wikicommons.files__files,dictzone__general,mymemory translated__general,wordnik__other,tootfinder__social media,wallhaven__images,wttr.in__other,brave__general,brave.images__images,brave.videos__videos,brave.news__news,bt4g__files");
    params.put("enabled_engines","360search__general,360search videos__videos,baidu__general,baidu images__images,baidu kaifa__it,chinaso news__other,chinaso images__other,chinaso videos__other,iqiyi__videos,sogou__general,sogou videos__videos,sogou wechat__news");
    params.put("format","json");
    return params;
  }

  @Data
  public static class SearchXNGResult {
    private String link;
    private String title;
    private String content;
  }
}
