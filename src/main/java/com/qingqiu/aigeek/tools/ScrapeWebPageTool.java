package com.qingqiu.aigeek.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.V;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.annotation.Configuration;

/**
 * 网页抓取工具
 */
@Configuration
public class ScrapeWebPageTool {

    @Tool("Scrape the content of a web page")
    public String scrapeWebPageTool(@P(value = "URL of the web page to scrape") String url) {
        try {
            Document document = Jsoup.connect(url).get();
            return document.html();
        } catch (Exception e) {
            return "Error scraping web page: " + e.getMessage();
        }
    }
}
