package com.qingqiu.openchat;

import com.qingqiu.openchat.domain.dto.ChatMessageDTO;
import com.qingqiu.openchat.domain.entity.User;
import com.qingqiu.openchat.domain.request.CreateChatMessageRequest;
import com.qingqiu.openchat.domain.request.CreateChatSessionRequest;
import com.qingqiu.openchat.domain.request.CreateKnowledgeBaseRequest;
import com.qingqiu.openchat.domain.request.CreateModelProviderRequest;
import com.qingqiu.openchat.domain.request.UpdateChatMessageRequest;
import com.qingqiu.openchat.domain.request.UpdateChatSessionRequest;
import com.qingqiu.openchat.domain.request.UpdateKnowledgeBaseRequest;
import com.qingqiu.openchat.domain.request.UpdateModelProviderRequest;
import com.qingqiu.openchat.domain.vo.ChatMessageVO;
import com.qingqiu.openchat.domain.vo.ChatSessionVO;
import com.qingqiu.openchat.domain.vo.LoginUserVO;
import com.qingqiu.openchat.domain.vo.ModelProviderVO;
import com.qingqiu.openchat.mapper.UserMapper;
import com.qingqiu.openchat.service.ChatMessageService;
import com.qingqiu.openchat.service.ChatSessionService;
import com.qingqiu.openchat.service.KnowledgeBaseService;
import com.qingqiu.openchat.service.ModelProviderService;
import com.qingqiu.openchat.service.UserService;
import com.qingqiu.openchat.util.UserContext;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class CrudServiceIntegrationTest {

    @Autowired
    private ChatSessionService chatSessionService;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    @Autowired
    private ModelProviderService modelProviderService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @AfterEach
    void tearDown() {
        UserContext.removeUser();
    }

    @Test
    void shouldCrudChatSessionAndMessageFlow() {
        UserContext.saveUser(910000000000L);

        String sessionId = chatSessionService.createChatSession(CreateChatSessionRequest.builder()
                .agentId(UUID.randomUUID().toString())
                .title("session-" + UUID.randomUUID().toString().substring(0, 8))
                .build());
        assertNotNull(sessionId);

        List<ChatSessionVO> sessionsByAgentId = chatSessionService.getChatSessionsByAgentId(chatSessionService.getChatSession(sessionId).getAgentId());
        assertTrue(sessionsByAgentId.stream().anyMatch(session -> sessionId.equals(session.getId())));

        assertTrue(chatSessionService.updateChatSession(sessionId, UpdateChatSessionRequest.builder().title("renamed").build()));
        assertEquals(sessionId, chatSessionService.getChatSession(sessionId).getId());

        String messageId = chatMessageService.createChatMessage(CreateChatMessageRequest.builder()
                .sessionId(sessionId)
                .role(ChatMessageDTO.RoleType.ASSISTANT)
                .content("hello world")
                .build());
        assertNotNull(messageId);

        List<ChatMessageVO> messages = chatMessageService.getChatMessagesBySessionId(sessionId);
        assertTrue(messages.stream().anyMatch(message -> messageId.equals(message.getId())));

        assertTrue(chatMessageService.updateChatMessage(messageId, UpdateChatMessageRequest.builder().content("updated").build()));
        assertTrue(chatMessageService.deleteChatMessage(messageId));
        assertTrue(chatSessionService.deleteChatSession(sessionId));
    }

    @Test
    void shouldCrudKnowledgeBaseAndModelProvider() {
        UserContext.saveUser(920000000000L);

        String knowledgeBaseId = knowledgeBaseService.createKnowledgeBase(CreateKnowledgeBaseRequest.builder()
                .name("kb-" + UUID.randomUUID().toString().substring(0, 8))
                .description("knowledge base")
                .build());
        assertNotNull(knowledgeBaseId);

        assertTrue(knowledgeBaseService.updateKnowledgeBase(UpdateKnowledgeBaseRequest.builder()
                .knowledgeBaseId(knowledgeBaseId)
                .name("kb-renamed")
                .build()));
        assertTrue(knowledgeBaseService.deleteKnowledgeBase(knowledgeBaseId));

        assertTrue(modelProviderService.insert(CreateModelProviderRequest.builder()
                .modelName("glm-4.6")
                .providerType("zhipu")
                .baseUrl("https://example.com")
                .apiKey("secret")
                .maxTokens(4096)
                .build()));

        List<ModelProviderVO> providers = modelProviderService.query(null);
        assertTrue(providers.stream().anyMatch(provider -> provider.getUserId() != null && provider.getUserId().equals(920000000000L)));

        ModelProviderVO createdProvider = providers.stream()
                .filter(provider -> provider.getUserId() != null && provider.getUserId().equals(920000000000L))
                .findFirst()
                .orElseThrow();

        assertTrue(modelProviderService.update(UpdateModelProviderRequest.builder()
                .modelId(createdProvider.getModelId())
                .modelName("glm-4.6-pro")
                .build()));
        assertTrue(modelProviderService.delete(createdProvider.getModelId()));
    }

    @Test
    void shouldReturnCurrentUserInfo() {
        long userId = 930000000000L;
        User user = User.builder()
                .userId(userId)
                .userName("tester")
                .mail("tester@example.com")
                .password("password")
                .role("user")
                .status(1)
                .isDeleted(0)
                .build();
        userMapper.insert(user);

        UserContext.saveUser(userId);
        LoginUserVO loginUserVO = userService.getLoginUser();

        assertNotNull(loginUserVO);
        assertEquals(userId, loginUserVO.getUserId());
        assertEquals("tester", loginUserVO.getUserName());
    }
}