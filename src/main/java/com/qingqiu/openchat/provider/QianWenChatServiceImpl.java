package com.qingqiu.openchat.provider;


import com.qingqiu.openchat.config.MyChatModelListener;
import com.qingqiu.openchat.domain.entity.ModelProvider;
import com.qingqiu.openchat.domain.request.ChatRequest;
import com.qingqiu.openchat.enums.ChatModeType;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import jakarta.annotation.Resource;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * qianWenAI服务调用
 *
 * @author ageerle@163.com
 * @date 2025/12/13
 */
@Service
@Slf4j
public class QianWenChatServiceImpl implements AbstractChatService {

    @Override
    public StreamingChatModel buildStreamingChatModel(ModelProvider chatModelVo,
        ChatRequest chatRequest) {
        return QwenStreamingChatModel.builder()
                .apiKey(chatModelVo.getApiKey())
                .modelName(chatModelVo.getModelName())
                .listeners(List.of(new MyChatModelListener()))
                .build();
    }

    @Override
    public String getProviderName() {
        return ChatModeType.QIAN_WEN.getCode();
    }

}
