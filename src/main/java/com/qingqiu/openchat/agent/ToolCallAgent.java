package com.qingqiu.openchat.agent;

import cn.hutool.core.util.StrUtil;
import com.qingqiu.openchat.enums.AgentState;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.tool.ToolExecutor;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 处理工具调用的基础代理类，具体实现了 think 和 act 方法，可以用作创建实例的父类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReActAgent {

    private final List<ToolSpecification> availableTools;
    private final Map<String, ToolExecutor> toolExecutors;

    public ToolCallAgent(List<ToolSpecification> availableTools, Map<String, ToolExecutor> toolExecutors) {
        super();
        this.availableTools = availableTools;
        this.toolExecutors = toolExecutors;
    }

    /**
     * 处理当前状态并决定下一步行动
     *
     * @return 是否需要执行行动
     */
    @Override
    public boolean think() {
        // 1、校验提示词，拼接用户提示词
        if (StrUtil.isNotBlank(getNextStepPrompt())) {
            getMessageList().add(new UserMessage(getNextStepPrompt()));
        }
        // 2、调用 AI 大模型，获取工具调用结果
        ChatRequest request = ChatRequest.builder()
            .messages(getMessageList())
            .toolSpecifications(availableTools)
            .build();
        try {
            ChatResponse chatResponse = getChatModel().chat(request);
            // 3、解析工具调用结果，获取要调用的工具
            AiMessage aiMessage = chatResponse.aiMessage();
            // 获取要调用的工具列表
            List<ToolExecutionRequest> toolExecutionRequests = aiMessage.toolExecutionRequests();
            // 输出提示信息
            String result = aiMessage.text();
            log.info(getName() + "的思考：" + result);
            log.info(getName() + "选择了 " + toolExecutionRequests.size() + " 个工具来使用");
            String toolCallInfo = toolExecutionRequests.stream()
                    .map(toolCall -> String.format("工具名称：%s，参数：%s", toolCall.name(), toolCall.arguments()))
                    .collect(Collectors.joining("\n"));
            log.info(toolCallInfo);
            // 如果不需要调用工具，返回 false
            if (toolExecutionRequests.isEmpty()) {
                // 只有不调用工具时，才需要手动记录助手消息
                getMessageList().add(aiMessage);
                return false;
            } else {
                // 需要调用工具时，无需记录助手消息，因为调用工具时会自动记录
                return true;
            }
        } catch (Exception e) {
            log.error(getName() + "的思考过程遇到了问题：" + e.getMessage());
            getMessageList().add(new AiMessage("处理时遇到了错误：" + e.getMessage()));
            return false;
        }
    }

    /**
     * 执行工具调用并处理结果
     *
     * @return 执行结果
     */
    @Override
    public String act() {
        ChatRequest request = ChatRequest.builder()
            .messages(getMessageList())
            .toolSpecifications(availableTools)
            .build();
        ChatResponse response = getChatModel().chat(request);
        AiMessage aiMessage = response.aiMessage();
        if (!aiMessage.hasToolExecutionRequests()) {
            return "没有工具需要调用";
        }
        // 调用工具
        for (ToolExecutionRequest toolRequest : aiMessage.toolExecutionRequests()) {
            ToolExecutor executor = toolExecutors.get(toolRequest.name());
            if (executor == null) {
                log.warn("未找到工具执行器：{}", toolRequest.name());
                continue;
            }
            if( "doTerminate".equals(toolRequest.name())){
                setState(AgentState.FINISHED);
            }
            ToolExecutionRequest toolExecutionRequest = ToolExecutionRequest.builder()
                .arguments(toolRequest.arguments()).name(toolRequest.name()).build();
            String result = executor.execute(toolExecutionRequest,toolRequest.id());
            getMessageList().add(new dev.langchain4j.data.message.ToolExecutionResultMessage(toolRequest.id(), toolRequest.name(), result));
            log.info("工具 {} 返回结果：{}", toolRequest.name(), result);
        }
        return "工具执行完成";
    }
}
