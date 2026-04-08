package com.qingqiu.openchat.agent;

import dev.langchain4j.model.chat.StreamingChatModel;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ChatClientRegistry {

    private final Map<String, StreamingChatModel> chatClients;

    public ChatClientRegistry(Map<String, StreamingChatModel> chatClients) {
        this.chatClients = chatClients;
    }

    public StreamingChatModel get(String key) {
        return chatClients.get(key);
    }
}
