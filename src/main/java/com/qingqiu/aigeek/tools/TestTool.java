package com.qingqiu.aigeek.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.output.structured.Description;
import org.springframework.context.annotation.Configuration;

/**
 * 测试工具类
 */
@Configuration
@Description("测试工具")
public class TestTool {

    @Tool("根据问题返回tom的年龄或性别")
    public String testTool(@P(value = "question") String question) {
        return "tom is 18 and sex is male";
    }
}
