package com.qingqiu.aigeek.config;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: QingQiu
 * @date: 2025/7/11
 * @description: mcp 工具调用类
 * TODO 测试
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mcp")
public class McpToolProviderConfig {

  private String apiKey;

//  @Bean
  public McpToolProvider mcpToolProvider() {
    McpTransport stdioTransport = new StdioMcpTransport.Builder()
        .command(List.of("/usr/bin/npm", "exec", "@modelcontextprotocol/server-everything@0.6.2"))
        .logEvents(true)
        .build();

    McpTransport httpTransport = new HttpMcpTransport.Builder()
        .sseUrl("https://open.bigmodel.cn/api/mcp/web_search/sse?Authorization=" + apiKey)
        // 开启日志，查看更多信息
        .logRequests(true)
        .logResponses(true)
        .build();
    // 创建 MCP 客户端
    McpClient httpMcpClient = new DefaultMcpClient.Builder()
        .key("QingQiuMcpClient")
        .transport(httpTransport)
        .build();
    McpClient stdioMcpClient = new DefaultMcpClient.Builder()
        .key("QingQiuMcpClient")
        .transport(stdioTransport)
        .build();
    // 从 MCP 客户端获取工具
    return McpToolProvider.builder()
        .mcpClients(httpMcpClient, stdioMcpClient)
        .build();
  }
}
