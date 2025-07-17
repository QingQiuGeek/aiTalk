package com.qingqiu.aigeek.ai.chatMemoryStore;

import static com.qingqiu.aigeek.constant.Common.FILE_SAVE_DIR;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.objenesis.strategy.StdInstantiatorStrategy;
import java.io.File;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * @author: QingQiu
 * @date: 2025/7/11
 * @description: 基于文件持久化的对话记忆
 */
@Component
public class FileMemoryStore implements ChatMemoryStore {

    private static final Kryo kryo = new Kryo();

    static {
        kryo.setRegistrationRequired(false);
        // 设置实例化策略
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    // 构造对象时，指定文件保存目录
    public FileMemoryStore() {
        File baseDir = new File(FILE_SAVE_DIR);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
    }

    @Override
    public void deleteMessages(Object memoryId) {
        File file = getConversationFile((String)memoryId);
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        return getOrCreateConversation((String)memoryId);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        List<ChatMessage> conversationHistory = getOrCreateConversation((String)memoryId);
        conversationHistory.addAll(messages);
        saveConversation((String)memoryId, conversationHistory);
    }

    private List<ChatMessage> getOrCreateConversation(String memoryId) {
        File file = getConversationFile(memoryId);
        List<ChatMessage> messages = new ArrayList<>();
        if (file.exists()) {
            try (Input input = new Input(new FileInputStream(file))) {
                messages = kryo.readObject(input, ArrayList.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return messages;
    }

    private void saveConversation(String memoryId, List<ChatMessage> messages) {
        File file = getConversationFile(memoryId);
        try (Output output = new Output(new FileOutputStream(file))) {
            kryo.writeObject(output, messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getConversationFile(String memoryId) {
        return new File(FILE_SAVE_DIR, memoryId + ".kryo");
    }
}
