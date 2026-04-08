package com.qingqiu.openchat.agent;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.tool.ToolExecutor;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * AI 超级智能体（拥有自主规划能力，可以直接使用）
 */
@Component
public class QingManus extends ToolCallAgent {

    public QingManus(List<ToolSpecification> allTools, Map<String, ToolExecutor> toolExecutors, ChatModel chatModel) {
        super(allTools, toolExecutors);
        this.setName("yuManus");
        String SYSTEM_PROMPT = """
                You are QingManus, an all-capable AI assistant, aimed at solving any task presented by the user.
                You have various tools at your disposal that you can call upon to efficiently complete complex requests.
                """;
        this.setSystemPrompt(SYSTEM_PROMPT);
        String NEXT_STEP_PROMPT = """
                Based on user needs, proactively select the most appropriate tool or combination of tools.
                For complex tasks, you can break down the problem and use different tools step by step to solve it.
                After using each tool, clearly explain the execution results and suggest the next steps.
                If you want to stop the interaction at any point, use the `terminate` tool/function call.
                """;
        this.setNextStepPrompt(NEXT_STEP_PROMPT);
        this.setMaxSteps(10);
        this.setChatModel(chatModel);
    }
}
