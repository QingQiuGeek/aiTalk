package com.qingqiu.aigeek.config;

import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: QingQiu
 * @date: 2025/7/15 18:54
 * @description:
 */
@Configuration
public class DocumentConfig {
  /**
   * 文档解析器，使用apache tika解析
   * 默认提供的TextDocumentParser 仅处理 纯文本 文件（TXT、HTML、Markdown 等）
   * ApacheTikaDocumentParser ，支持 几乎所有常见格式：PDF、DOC/DOCX、PPT/PPTX、XLS/XLSX、HTML、TXT、EPUB、ZIP 等
   * @return
   */
  @Bean
  public DocumentParser documentParser(){
    return new ApacheTikaDocumentParser();
  }

  /**
   * 文档切分器，使用段落递归切分
   * @return
   */
  @Bean
  public DocumentSplitter documentSplitter(){
    // 段落最大长度300，段落间最大重叠字符20
    return DocumentSplitters.recursive(300, 20);
  }
}

