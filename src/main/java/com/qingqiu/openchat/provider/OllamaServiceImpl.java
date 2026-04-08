package com.qingqiu.openchat.provider;


import com.qingqiu.openchat.config.MyChatModelListener;
import com.qingqiu.openchat.domain.entity.ModelProvider;
import com.qingqiu.openchat.domain.request.ChatRequest;
import com.qingqiu.openchat.enums.ChatModeType;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * OllamaAI服务调用
 *
 * @author ageerle@163.com
 * @date 2025/12/13
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OllamaServiceImpl implements AbstractChatService {

    @Override
    public StreamingChatModel buildStreamingChatModel(ModelProvider chatModelVo, ChatRequest chatRequest) {
        return OllamaStreamingChatModel.builder()
                .baseUrl(chatModelVo.getBaseUrl())
                .modelName(chatModelVo.getModelName())
                .listeners(List.of(new MyChatModelListener()))
                .build();
    }

    @Override
    public String getProviderName() {
        return ChatModeType.OLLAMA.getCode();
    }
}
