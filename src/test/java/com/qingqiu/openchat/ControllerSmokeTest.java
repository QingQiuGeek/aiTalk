package com.qingqiu.openchat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qingqiu.openchat.controller.AgentController;
import com.qingqiu.openchat.controller.ChatMessageController;
import com.qingqiu.openchat.controller.ChatSessionController;
import com.qingqiu.openchat.controller.KnowledgeBaseController;
import com.qingqiu.openchat.controller.ModelProviderController;
import com.qingqiu.openchat.controller.SseController;
import com.qingqiu.openchat.controller.UserController;
import com.qingqiu.openchat.domain.dto.AgentDTO;
import com.qingqiu.openchat.domain.dto.ChatMessageDTO;
import com.qingqiu.openchat.domain.dto.UserLoginDTO;
import com.qingqiu.openchat.domain.dto.UserRegisterDTO;
import com.qingqiu.openchat.domain.dto.UserRegisterMailDTO;
import com.qingqiu.openchat.domain.request.CreateAgentRequest;
import com.qingqiu.openchat.domain.request.CreateChatMessageRequest;
import com.qingqiu.openchat.domain.request.CreateChatSessionRequest;
import com.qingqiu.openchat.domain.request.CreateKnowledgeBaseRequest;
import com.qingqiu.openchat.domain.request.CreateModelProviderRequest;
import com.qingqiu.openchat.domain.request.UpdateAgentRequest;
import com.qingqiu.openchat.domain.request.UpdateChatMessageRequest;
import com.qingqiu.openchat.domain.request.UpdateChatSessionRequest;
import com.qingqiu.openchat.domain.request.UpdateKnowledgeBaseRequest;
import com.qingqiu.openchat.domain.request.UpdateModelProviderRequest;
import com.qingqiu.openchat.domain.vo.AgentVO;
import com.qingqiu.openchat.domain.vo.ChatMessageVO;
import com.qingqiu.openchat.domain.vo.ChatSessionVO;
import com.qingqiu.openchat.domain.vo.KnowledgeBaseVO;
import com.qingqiu.openchat.domain.vo.LoginUserVO;
import com.qingqiu.openchat.domain.vo.ModelProviderVO;
import com.qingqiu.openchat.service.AgentService;
import com.qingqiu.openchat.service.ChatMessageService;
import com.qingqiu.openchat.service.ChatSessionService;
import com.qingqiu.openchat.service.KnowledgeBaseService;
import com.qingqiu.openchat.service.ModelProviderService;
import com.qingqiu.openchat.service.SseService;
import com.qingqiu.openchat.service.UserService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ControllerSmokeTest {

    @Mock
    private AgentService agentService;

    @Mock
    private ChatMessageService chatMessageService;

    @Mock
    private ChatSessionService chatSessionService;

    @Mock
    private KnowledgeBaseService knowledgeBaseService;

    @Mock
    private ModelProviderService modelProviderService;

    @Mock
    private SseService sseService;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        AgentController agentController = new AgentController();
        ChatMessageController chatMessageController = new ChatMessageController();
        ChatSessionController chatSessionController = new ChatSessionController();
        KnowledgeBaseController knowledgeBaseController = new KnowledgeBaseController();
        ModelProviderController modelProviderController = new ModelProviderController();
        SseController sseController = new SseController();
        UserController userController = new UserController();

        ReflectionTestUtils.setField(agentController, "agentService", agentService);
        ReflectionTestUtils.setField(chatMessageController, "chatMessageService", chatMessageService);
        ReflectionTestUtils.setField(chatSessionController, "chatSessionService", chatSessionService);
        ReflectionTestUtils.setField(knowledgeBaseController, "knowledgeBaseService", knowledgeBaseService);
        ReflectionTestUtils.setField(modelProviderController, "modelProviderService", modelProviderService);
        ReflectionTestUtils.setField(sseController, "sseService", sseService);
        ReflectionTestUtils.setField(userController, "userService", userService);

        mockMvc = MockMvcBuilders.standaloneSetup(
                        agentController,
                        chatMessageController,
                        chatSessionController,
                        knowledgeBaseController,
                        modelProviderController,
                        sseController,
                        userController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void shouldBindAgentRoutes() throws Exception {
        when(agentService.getAgents()).thenReturn(List.of());
        when(agentService.createAgent(any())).thenReturn("agent-1");
        when(agentService.deleteAgent(anyString())).thenReturn(Boolean.TRUE);
        when(agentService.updateAgent(any())).thenReturn(Boolean.TRUE);

        CreateAgentRequest createRequest = CreateAgentRequest.builder()
                .name("assistant")
                .description("desc")
                .systemPrompt("sys")
                .model("glm-4.6")
                .allowedTools(List.of("search"))
                .allowedKbs(List.of("kb-1"))
                .chatOptions(AgentDTO.ChatOptions.defaultOptions())
                .build();

        UpdateAgentRequest updateRequest = UpdateAgentRequest.builder()
                .agentId("agent-1")
                .name("assistant-v2")
                .build();

        mockMvc.perform(get("/agent"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/agent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());
        mockMvc.perform(put("/agent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/agent/{agentId}", "agent-1"))
                .andExpect(status().isOk());

        verify(agentService).getAgents();
        verify(agentService).createAgent(any());
        verify(agentService).updateAgent(any());
        verify(agentService).deleteAgent("agent-1");
    }

    @Test
    void shouldBindChatSessionRoutes() throws Exception {
        when(chatSessionService.getChatSessions()).thenReturn(List.of());
        when(chatSessionService.getChatSession("session-1")).thenReturn(ChatSessionVO.builder().id("session-1").build());
        when(chatSessionService.getChatSessionsByAgentId("agent-1")).thenReturn(List.of(ChatSessionVO.builder().id("session-1").build()));
        when(chatSessionService.createChatSession(any())).thenReturn("session-1");
        when(chatSessionService.deleteChatSession("session-1")).thenReturn(Boolean.TRUE);
        when(chatSessionService.updateChatSession(anyString(), any())).thenReturn(Boolean.TRUE);

        CreateChatSessionRequest createRequest = CreateChatSessionRequest.builder()
                .agentId("agent-1")
                .title("new session")
                .build();
        UpdateChatSessionRequest updateRequest = UpdateChatSessionRequest.builder()
                .title("renamed")
                .build();

        mockMvc.perform(get("/chat-sessions"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/chat-sessions/{chatSessionId}", "session-1"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/chat-sessions/agent/{agentId}", "agent-1"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/chat-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());
        mockMvc.perform(put("/chat-sessions/{chatSessionId}", "session-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/chat-sessions/{chatSessionId}", "session-1"))
                .andExpect(status().isOk());

        verify(chatSessionService).getChatSessions();
        verify(chatSessionService).getChatSession("session-1");
        verify(chatSessionService).getChatSessionsByAgentId("agent-1");
        verify(chatSessionService).createChatSession(any());
        verify(chatSessionService).updateChatSession(anyString(), any());
        verify(chatSessionService).deleteChatSession("session-1");
    }

    @Test
    void shouldBindChatMessageRoutes() throws Exception {
        when(chatMessageService.getChatMessagesBySessionId("session-1")).thenReturn(List.of());
        when(chatMessageService.createChatMessage(any(CreateChatMessageRequest.class))).thenReturn("message-1");
        when(chatMessageService.deleteChatMessage("message-1")).thenReturn(Boolean.TRUE);
        when(chatMessageService.updateChatMessage(anyString(), any())).thenReturn(Boolean.TRUE);

        CreateChatMessageRequest createRequest = CreateChatMessageRequest.builder()
                .sessionId("session-1")
                .role(ChatMessageDTO.RoleType.ASSISTANT)
                .content("hello")
                .build();
        UpdateChatMessageRequest updateRequest = UpdateChatMessageRequest.builder()
                .content("updated")
                .build();

        mockMvc.perform(get("/chat-messages/session/{sessionId}", "session-1"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/chat-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());
        mockMvc.perform(put("/chat-messages/{chatMessageId}", "message-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/chat-messages/{chatMessageId}", "message-1"))
                .andExpect(status().isOk());

        verify(chatMessageService).getChatMessagesBySessionId("session-1");
        verify(chatMessageService).createChatMessage(any(CreateChatMessageRequest.class));
        verify(chatMessageService).updateChatMessage(anyString(), any());
        verify(chatMessageService).deleteChatMessage("message-1");
    }

    @Test
    void shouldBindKnowledgeBaseRoutes() throws Exception {
        when(knowledgeBaseService.getKnowledgeBases()).thenReturn(List.of());
        when(knowledgeBaseService.createKnowledgeBase(any())).thenReturn("kb-1");
        when(knowledgeBaseService.deleteKnowledgeBase("kb-1")).thenReturn(Boolean.TRUE);
        when(knowledgeBaseService.updateKnowledgeBase(any())).thenReturn(Boolean.TRUE);

        CreateKnowledgeBaseRequest createRequest = CreateKnowledgeBaseRequest.builder()
                .name("kb")
                .description("desc")
                .build();
        UpdateKnowledgeBaseRequest updateRequest = UpdateKnowledgeBaseRequest.builder()
                .knowledgeBaseId("kb-1")
                .name("kb-v2")
                .build();

        mockMvc.perform(get("/knowledge-bases"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/knowledge-bases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());
        mockMvc.perform(put("/knowledge-bases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/knowledge-bases/{knowledgeBaseId}", "kb-1"))
                .andExpect(status().isOk());

        verify(knowledgeBaseService).getKnowledgeBases();
        verify(knowledgeBaseService).createKnowledgeBase(any());
        verify(knowledgeBaseService).updateKnowledgeBase(any());
        verify(knowledgeBaseService).deleteKnowledgeBase("kb-1");
    }

    @Test
    void shouldBindModelProviderRoutes() throws Exception {
        when(modelProviderService.query(any())).thenReturn(List.of());
        when(modelProviderService.insert(any())).thenReturn(Boolean.TRUE);
        when(modelProviderService.update(any())).thenReturn(Boolean.TRUE);
        when(modelProviderService.delete(1L)).thenReturn(Boolean.TRUE);

        CreateModelProviderRequest createRequest = CreateModelProviderRequest.builder()
                .modelId(1L)
                .modelName("glm-4.6")
                .providerType("zhipu")
                .baseUrl("https://example.com")
                .apiKey("secret")
                .maxTokens(8192)
                .build();
        UpdateModelProviderRequest updateRequest = UpdateModelProviderRequest.builder()
                .modelId(1L)
                .modelName("glm-4.6-pro")
                .build();

        mockMvc.perform(get("/model/provider"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/model/provider")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());
        mockMvc.perform(put("/model/provider")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/model/provider/{id}", 1L))
                .andExpect(status().isOk());

        verify(modelProviderService).query(any());
        verify(modelProviderService).insert(any());
        verify(modelProviderService).update(any());
        verify(modelProviderService).delete(1L);
    }

    @Test
    void shouldBindUserRoutes() throws Exception {
        when(userService.login(any())).thenReturn(new LoginUserVO());
        when(userService.register(any())).thenReturn(new LoginUserVO());
        when(userService.logout(any())).thenReturn(Boolean.TRUE);
        when(userService.getLoginUser()).thenReturn(new LoginUserVO());
        when(userService.sendRegisterCode(any())).thenReturn(Boolean.TRUE);

        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setMail("demo@example.com");
        loginDTO.setPassword("password123");

        UserRegisterDTO registerDTO = new UserRegisterDTO();
        registerDTO.setMail("demo@example.com");
        registerDTO.setPassword("password123");
        registerDTO.setRePassword("password123");
        registerDTO.setCode("123456");

        UserRegisterMailDTO mailDTO = new UserRegisterMailDTO();
        mailDTO.setMail("demo@example.com");

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk());
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk());
        mockMvc.perform(post("/user/logout"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/user"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/user/register-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mailDTO)))
                .andExpect(status().isOk());

        verify(userService).login(any());
        verify(userService).register(any());
        verify(userService).logout(any());
        verify(userService).getLoginUser();
        verify(userService).sendRegisterCode(any());
    }

    @Test
    void shouldBindSseRoute() throws Exception {
        when(sseService.connect("session-1")).thenReturn(new SseEmitter());

        mockMvc.perform(get("/sse/connect/{chatSessionId}", "session-1"))
                .andExpect(request().asyncStarted());

        verify(sseService).connect("session-1");
    }
}
