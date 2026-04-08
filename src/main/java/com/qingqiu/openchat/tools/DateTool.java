package com.qingqiu.openchat.tools;

import dev.langchain4j.agent.tool.Tool;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class DateTool implements ITool {

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

    @Tool(name = "getDate", value = "Return current date")
    public String getDate() {
        return LocalDate.now().toString();
    }
}
