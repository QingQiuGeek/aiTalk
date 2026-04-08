package com.qingqiu.openchat.tools;

import dev.langchain4j.agent.tool.Tool;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class TestTool implements ITool {

    @Override
    public String getName() {
        return "dateTool";
    }

    @Override
    public String getDescription() {
        return "Get current date";
    }

    @Override
    public ToolType getType() {
        return ToolType.OPTIONAL;
    }

    @Tool(name = "testTool", value = "test")
    public String testTool() {
        System.out.println("testTool called");
        return "testTool called";
    }
}
