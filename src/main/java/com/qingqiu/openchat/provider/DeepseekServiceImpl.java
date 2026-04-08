package com.qingqiu.openchat.provider;


import com.qingqiu.openchat.config.MyChatModelListener;
import com.qingqiu.openchat.domain.entity.ModelProvider;
import com.qingqiu.openchat.domain.request.ChatRequest;
import com.qingqiu.openchat.enums.ChatModeType;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * Deepseek服务调用
 *
 * @author xiaoen
 * @date 2026/3/17
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DeepseekServiceImpl implements AbstractChatService {

    @Override
    public StreamingChatModel buildStreamingChatModel(ModelProvider chatModelVo, ChatRequest chatRequest) {
        return OpenAiStreamingChatModel.builder()
            .baseUrl(chatModelVo.getBaseUrl())
            .apiKey(chatModelVo.getApiKey())
            .modelName(chatModelVo.getModelName())
            .listeners(List.of(new MyChatModelListener()))
            .returnThinking(chatRequest.getEnableThinking())
            .build();
    }


    @Override
    public String getProviderName() {
        return ChatModeType.DEEP_SEEK.getCode();
    }

}
