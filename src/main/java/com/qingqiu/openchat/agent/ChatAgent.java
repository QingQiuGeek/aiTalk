package com.qingqiu.openchat.agent;

import com.qingqiu.openchat.convert.ChatMessageConverter;
import com.qingqiu.openchat.domain.SseMessage;
import com.qingqiu.openchat.domain.dto.ChatMessageDTO;
import com.qingqiu.openchat.domain.dto.KnowledgeBaseDTO;
import com.qingqiu.openchat.domain.vo.ChatMessageVO;
import com.qingqiu.openchat.enums.AgentState;
import com.qingqiu.openchat.service.ChatMessageService;
import com.qingqiu.openchat.service.SseService;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Slf4j
public class ChatAgent {
    // 智能体 ID
    private String agentId;

    // 名称
    private String name;

    // 描述
    private String description;

    // 默认系统提示词
    private String systemPrompt;

    // 交互实例
    private ChatModel chatModel;

    // 状态
    private AgentState agentState;

    // 可用的工具
    private LangChainToolExecutor toolExecutor;

    // 可访问的知识库
    private List<KnowledgeBaseDTO> availableKbs;

    // 模型的聊天记录
    private Deque<ChatMessage> chatMemory;

    private int maxMessages;

    // 模型的聊天会话 ID
    private String chatSessionId;

    // 最多循环次数
    private static final Integer MAX_STEPS = 20;

    private static final Integer DEFAULT_MAX_MESSAGES = 20;

    // SSE 服务, 用于发送消息给前端
    private SseService sseService;

    private ChatMessageConverter chatMessageConverter;

    private ChatMessageService chatMessageService;

    // 最后一次的 AiMessage
    private AiMessage lastAiMessage;

    // AI 返回的，已经持久化，但是需要 sse 发给前端的消息
    private final List<ChatMessageDTO> pendingChatMessages = new ArrayList<>();

    public ChatAgent() {
    }

    public ChatAgent(String agentId,
        String name,
        String description,
        String systemPrompt,
        ChatModel chatModel,
        Integer maxMessages,
        List<ChatMessage> memory,
        LangChainToolExecutor toolExecutor,
        List<KnowledgeBaseDTO> availableKbs,
        String chatSessionId,
        SseService sseService,
        ChatMessageService chatMessageService,
        ChatMessageConverter chatMessageConverter
    ) {
        this.agentId = agentId;
        this.name = name;
        this.description = description;
        this.systemPrompt = systemPrompt;

        this.chatModel = chatModel;

        this.toolExecutor = toolExecutor;
        this.availableKbs = availableKbs;
        this.maxMessages = maxMessages == null ? DEFAULT_MAX_MESSAGES : maxMessages;

        this.chatSessionId = chatSessionId;
        this.sseService = sseService;

        this.chatMessageService = chatMessageService;
        this.chatMessageConverter = chatMessageConverter;

        this.agentState = AgentState.IDLE;

        this.chatMemory = new ArrayDeque<>();
        if (memory != null) {
            for (ChatMessage message : memory) {
                addToMemory(message);
            }
        }

        // 添加系统提示
        if (StringUtils.hasLength(systemPrompt)) {
            addToMemory(SystemMessage.from(systemPrompt));
        }
    }

    private void addToMemory(ChatMessage message) {
        this.chatMemory.addLast(message);
        while (this.chatMemory.size() > this.maxMessages) {
            this.chatMemory.removeFirst();
        }
    }

    // 打印工具调用信息
    private void logToolCalls(List<ToolExecutionRequest> toolCalls) {
        if (toolCalls == null || toolCalls.isEmpty()) {
            log.info("\n\n[ToolCalling] 无工具调用");
            return;
        }
        String logMessage = IntStream.range(0, toolCalls.size())
            .mapToObj(i -> {
                ToolExecutionRequest call = toolCalls.get(i);
                return String.format(
                    "[ToolCalling #%d]\n- name      : %s\n- arguments : %s",
                    i + 1,
                    call.name(),
                    call.arguments()
                );
            })
            .collect(Collectors.joining("\n\n"));
        log.info("\n\n========== Tool Calling ==========\n{}\n=================================\n", logMessage);
    }

    // 持久化 Message, 返回 chatMessageId
    // 需要 Agent 持久化的 Message 子类有以下两类
    // AssistantMessage
    // ToolResponseMessage

    // SystemMessage 不需要持久化
    // UserMessage 在每次用户发送问题之间就已经持久化过了
    private void saveMessage(ChatMessage message) {
        ChatMessageDTO.ChatMessageDTOBuilder builder = ChatMessageDTO.builder();
        if (message instanceof AiMessage assistantMessage) {
            List<ChatMessageDTO.ToolCall> toolCalls = assistantMessage.toolExecutionRequests() == null
                ? List.of()
                : assistantMessage.toolExecutionRequests().stream()
                    .map(req -> ChatMessageDTO.ToolCall.builder()
                        .id(req.id())
                        .name(req.name())
                        .arguments(req.arguments())
                        .build())
                    .toList();

            ChatMessageDTO chatMessageDTO = builder.role(ChatMessageDTO.RoleType.ASSISTANT)
                .content(assistantMessage.text())
                .sessionId(this.chatSessionId)
                .metadata(ChatMessageDTO.MetaData.builder()
                    .toolCalls(toolCalls)
                    .build())
                .build();
            String chatMessageId = chatMessageService.createChatMessage(chatMessageDTO);
            chatMessageDTO.setId(chatMessageId);
            pendingChatMessages.add(chatMessageDTO);
        } else if (message instanceof ToolExecutionResultMessage toolResponseMessage) {
            ChatMessageDTO chatMessageDTO = builder.role(ChatMessageDTO.RoleType.TOOL)
                .content(toolResponseMessage.text())
                .sessionId(this.chatSessionId)
                .metadata(ChatMessageDTO.MetaData.builder()
                    .toolResponse(ChatMessageDTO.ToolResponse.builder()
                        .id(toolResponseMessage.id())
                        .name(toolResponseMessage.toolName())
                        .responseData(toolResponseMessage.text())
                        .build())
                    .build())
                .build();
            String chatMessageId = chatMessageService.createChatMessage(chatMessageDTO);
            chatMessageDTO.setId(chatMessageId);
            pendingChatMessages.add(chatMessageDTO);
        } else {
            throw new IllegalArgumentException("不支持的 Message 类型: " + message.getClass().getName());
        }
    }

    // 刷新 pendingMessages, 将数据通过 sse 发送给前端
    private void refreshPendingMessages() {
        for (ChatMessageDTO message : pendingChatMessages) {
            ChatMessageVO vo = chatMessageConverter.toVO(message);
            SseMessage sseMessage = SseMessage.builder()
                .type(SseMessage.Type.AI_GENERATED_CONTENT)
                .payload(SseMessage.Payload.builder()
                    .message(vo)
                    .build())
                .metadata(SseMessage.Metadata.builder()
                    .chatMessageId(message.getId())
                    .build())
                .build();
            sseService.send(this.chatSessionId, sseMessage);
        }
        pendingChatMessages.clear();
    }

    // thinkPrompt 应该放到 system 中还是
    private boolean think() {
        String thinkPrompt = """
                现在你是一个智能的的具体「决策模块」
                请根据当前对话上下文，决定下一步的动作。
                                \s
                【额外信息】
                - 你目前拥有的知识库列表以及描述：%s
                - 如果有缺失的上下文时，优先从知识库中进行搜索
                """.formatted(this.availableKbs);

        List<ChatMessage> requestMessages = new ArrayList<>(this.chatMemory);
        requestMessages.add(SystemMessage.from(thinkPrompt));

        ChatRequest request = ChatRequest.builder()
            .messages(requestMessages)
            .toolSpecifications(this.toolExecutor.getToolSpecifications())
            .build();

        ChatResponse response = this.chatModel.chat(request);
        Assert.notNull(response, "Chat response cannot be null");

        AiMessage output = response.aiMessage();
        Assert.notNull(output, "AI message cannot be null");

        List<ToolExecutionRequest> toolCalls = output.toolExecutionRequests();

        this.lastAiMessage = output;
        addToMemory(output);

        // 保存
        saveMessage(output);
        // 刷新消息，将 AI 回复通过 SSE 发送给前端
        refreshPendingMessages();

        // 打印工具调用
        logToolCalls(toolCalls);

        // 如果工具调用不为空，则进入执行阶段
        return output.hasToolExecutionRequests();
    }

    // 执行
    private void execute() {
        Assert.notNull(this.lastAiMessage, "Last AI message cannot be null");

        if (!this.lastAiMessage.hasToolExecutionRequests()) {
            return;
        }

        List<ToolExecutionResultMessage> toolResults = new ArrayList<>();
        for (ToolExecutionRequest request : this.lastAiMessage.toolExecutionRequests()) {
            String result = toolExecutor.execute(request);
            ToolExecutionResultMessage resultMessage = ToolExecutionResultMessage.from(request, result);
            toolResults.add(resultMessage);
            addToMemory(resultMessage);
            saveMessage(resultMessage);
        }

        String collect = toolResults
            .stream()
            .map(resp -> "工具" + resp.toolName() + "的返回结果为：" + resp.text())
            .collect(Collectors.joining("\n"));

        log.info("工具调用结果：{}", collect);

        refreshPendingMessages();

        if (toolResults.stream().anyMatch(resp -> "terminate".equals(resp.toolName()))) {
            this.agentState = AgentState.FINISHED;
            log.info("任务结束");
        }
    }

    // 单个步骤模板
    private void step() {
        if (think()) {
            execute();
        } else { // 没有工具调用
            agentState = AgentState.FINISHED;
        }
    }

    // 运行
    public void run() {
        if (agentState != AgentState.IDLE) {
            throw new IllegalStateException("Agent is not idle");
        }

        try {
            agentState = AgentState.RUNNING;
            for (int i = 0; i < MAX_STEPS && agentState != AgentState.FINISHED; i++) {
                // 当前步骤，用于实现 Agent Loop
                int currentStep = i + 1;
                step();
                if (currentStep >= MAX_STEPS) {
                    agentState = AgentState.FINISHED;
                    log.warn("Max steps reached, stopping agent");
                }
            }
            agentState = AgentState.FINISHED;
        } catch (Exception e) {
            agentState = AgentState.ERROR;
            log.error("Error running agent", e);
            throw new RuntimeException("Error running agent", e);
        }
    }

    @Override
    public String toString() {
        return "OpenAgent {" +
            "name = " + name + ",\n" +
            "description = " + description + ",\n" +
            "agentId = " + agentId + ",\n" +
            "systemPrompt = " + systemPrompt + "}";
    }
}
