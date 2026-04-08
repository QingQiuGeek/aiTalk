package com.qingqiu.openchat.provider;


import com.qingqiu.openchat.config.MyChatModelListener;
import com.qingqiu.openchat.domain.entity.ModelProvider;
import com.qingqiu.openchat.domain.request.ChatRequest;
import com.qingqiu.openchat.enums.ChatModeType;
import dev.langchain4j.community.model.zhipu.ZhipuAiStreamingChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * 智谱AI服务调用
 *
 * @author zengxb
 * @date 2026/02/26
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ZhiPuChatServiceImpl implements AbstractChatService {

    @Override
    public StreamingChatModel buildStreamingChatModel(ModelProvider chatModelVo, ChatRequest chatRequest) {
        return ZhipuAiStreamingChatModel.builder()
            .apiKey(chatModelVo.getApiKey())
            .model(chatModelVo.getModelName())
            .listeners(List.of(new MyChatModelListener()))
            .build();
    }

    @Override
    public String getProviderName() {
        return ChatModeType.ZHI_PU.getCode();
    }
}
