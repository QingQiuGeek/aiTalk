package com.qingqiu.aigeek;

import static com.alibaba.dashscope.utils.Constants.apiKey;
import static dev.langchain4j.internal.Utils.readBytes;
import static dev.langchain4j.model.chat.request.ResponseFormatType.JSON;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.assistants.Assistant;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import com.qingqiu.aigeek.service.AiAutoService;
import com.qingqiu.aigeek.service.AiManualService;
import com.qingqiu.aigeek.tools.ImageGenerateTextTool;
import com.qingqiu.aigeek.tools.PDFGenerationTool;
import com.qingqiu.aigeek.tools.TextGenerateImageTool;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenChatRequestParameters;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.ImageContent.DetailLevel;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchema;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.aggregator.DefaultContentAggregator;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.rag.query.transformer.DefaultQueryTransformer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

/**
 * @Author: QingQiu
 * @Date: 2025/7/11
 * @Description:
 */
@Slf4j
@SpringBootTest
public class ChatTest {

  @Resource
  private ChatModel chatModel;

  @Resource
  private StreamingChatModel streamingChatModel;

  @Resource
  DocumentSplitter documentSplitter;

  @Resource
  private EmbeddingModel embeddingModel;

  @Resource(name = "inMemoryStoreChatMemoryProvider")
  ChatMemoryProvider chatMemoryProvider;

  @Autowired
  @Qualifier("inMemoryEmbeddingStore")
  InMemoryEmbeddingStore<TextSegment> inMemoryEmbeddingStore;

  @Autowired
  @Qualifier("pgVectorEmbeddingStore")
  PgVectorEmbeddingStore pgVectorEmbeddingStore;

  @Autowired
  @Qualifier("disableWebSearchQueryRouterRetrievalAugmentor")
  RetrievalAugmentor disableWebSearchQueryRouterRetrievalAugmentor;

  @Autowired
  @Qualifier("enableWebSearchQueryRouterRetrievalAugmentor")
  RetrievalAugmentor enableWebSearchQueryRouterRetrievalAugmentor;

  @Autowired
  @Qualifier("inMemoryContentRetriever")
  ContentRetriever inMemoryContentRetriever;

  @Autowired
  @Qualifier("pgVectorContentRetriever")
  ContentRetriever pgVectorContentRetriever;

  @Resource
  ImageGenerateTextTool imageGenerateTextTool;

  @Resource
  TextGenerateImageTool textGenerateImageTool;

  /**
   * 从idea的环境变量中获取key
   * @return
   */
  public String getApiKey(){
    return System.getenv("DASHSCOPE_AI_KEY");
  }

  @Test
  public void modelToolTest(){
    AiManualService assistant = AiServices.builder(AiManualService.class)
        .streamingChatModel(streamingChatModel)
        .chatModel(chatModel)
        .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
        .chatMemoryProvider(chatMemoryProvider)
        .tools(imageGenerateTextTool,textGenerateImageTool)
        .build();
    Flux<String> flux = assistant.chatFlux("1",
        "你知道蔡徐坤吗，他长什么样？画一只穿着背带裤打篮球的鸡");
    String res = assistant.chatStr("1","你知道蔡徐坤吗，他长什么样？画一只穿着背带裤打篮球的鸡");
    log.info(res);
  }
  /*
   * 全模态大模型
   * https://help.aliyun.com/zh/model-studio/realtime?spm=a2c4g.11186623.0.0.6ea92c71s7OgtS#d6f3ba031di77
   * 该全模态大模型仅支持websocket调用，send()内容需要是json格式，具体看链接文档
   * */
  @Test
  public void fullMultiModal(){
    OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .build();

    Request request = new Request.Builder()
        .url("wss://dashscope.aliyuncs.com/api-ws/v1/realtime?model=qwen-omni-turbo-realtime")
        .addHeader("Authorization", "Bearer " + getApiKey())
        .build();

    WebSocketListener listener = new WebSocketListener() {
      @Override
      public void onClosed(@NotNull okhttp3.WebSocket webSocket, int code, @NotNull String reason) {
        log.info("close:{},{}",code,reason);
      }

      @Override
      public void onClosing(@NotNull okhttp3.WebSocket webSocket, int code,
          @NotNull String reason) {
        log.info("closing:{},{}",code,reason);
      }

      @Override
      public void onFailure(@NotNull okhttp3.WebSocket webSocket, @NotNull Throwable t,
          @Nullable Response response) {
        log.info("fail:{},{}",response.code(),t.toString());
      }

      @Override
      public void onMessage(@NotNull okhttp3.WebSocket webSocket, @NotNull ByteString bytes) {
        log.info("message:{}",bytes);

      }

      @Override
      public void onMessage(@NotNull okhttp3.WebSocket webSocket, @NotNull String text) {
        log.info("text:{}",text);

      }

      @Override
      public void onOpen(@NotNull okhttp3.WebSocket webSocket, @NotNull Response response) {
        log.info("open:{},{},{}",response.code(),response.message(),response.body());

      }
    };

    WebSocket ws = client.newWebSocket(request, listener);
    String json1 = "{\"type\":\"response.create\",\"response\":{\"instructions\":\"描述这个图片：https://dashscope.oss-cn-beijing.aliyuncs.com/images/tiger.png\",\"modalities\":[\"text\",\"audio\"]}}";
//    String json2 = "{\"type\":\"response.create\",\"response\":{\"instructions\":\"描述这个图片：https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/1d/79/20250714/ce62dca3/6d2cf934-28d4-4ee9-a0f3-b25c429571231931703893.png?Expires=1752580770&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=OXR%2FetTr2ja%2FKn5gIo9%2FYeIhcbI%3D\"\",\"modalities\":[\"text\",\"audio\"]}}";
//    String json3 = "{\"type\":\"response.create\",\"response\":{\"instructions\":\"描述这个图片：https://cdn.jsdelivr.net/gh/QingQiuGeek/imgRepo/picGo/202507141947638.webp\"\",\"modalities\":[\"text\",\"audio\"]}}";
    ws.send(json1);

    // 防止主线程退出，等待 WebSocket 事件处理
    try {
      Thread.sleep(100000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
//    关闭 OkHttp 客户端内部使用的线程池，当然也可以常驻，处理用户请求
    client.dispatcher().executorService().shutdown();
  }

  /**
   * 图生文
   * 支持图片转base64传给大模型识别 √
   */
  @Test
  public void imageGenerateText1(){
    MultiModalConversation conv = new MultiModalConversation();
    /* 1. 读取本地图片并转成 Base64 */
    String base64  = null;
    try {
      base64 = Base64.getEncoder()
          .encodeToString(new ClassPathResource("image/tiger.png").getInputStream().readAllBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    /* 2. 构建消息：Base64 直接写在 content 里 */
    MultiModalMessage userMsg = MultiModalMessage.builder()
        .role(Role.USER.getValue())
        .content(List.of(
            Map.of("image", "data:image/png;base64," + base64),
            Map.of("text",  "描述这几张图片")
        ))
        .build();

//    MultiModalMessage userMessage1 = MultiModalMessage.builder().role(Role.USER.getValue())
//        .content(Arrays.asList(
//            Collections.singletonMap("image", "https://dashscope.oss-cn-beijing.aliyuncs.com/images/dog_and_girl.jpeg"),
//            Collections.singletonMap("text", "描述这几张图片"))).build();
    MultiModalConversationParam param = MultiModalConversationParam.builder()
        .apiKey(getApiKey())
        .model("qwen-vl-plus")
        .message(userMsg)
        .build();
//    List<Object> messages = param.getMessages();
//    log.info(messages.toString());
//    MultiModalMessage userMessage2 = MultiModalMessage.builder().role(Role.USER.getValue())
//        .content(Arrays.asList(
//            Collections.singletonMap("image", "https://dashscope.oss-cn-beijing.aliyuncs.com/images/tiger.png"),
//            Collections.singletonMap("text", "描述这几张图片"))).build();
//    param.setMessages(List.of(userMessage2));
    MultiModalConversationResult result = null;
    try {
      result = conv.call(param);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    log.info("result:{}",result);
  }

  @Test
  public void imageGenerateText2(){
    String base64  = null;
    try {
      base64 = Base64.getEncoder()
          .encodeToString(new ClassPathResource("image/tiger.png").getInputStream().readAllBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    String string = imageGenerateTextTool.analyzeImage("data:image/png;base64,"+base64, "描述图片内容");
    log.info(string);
  }

  //文生图
  @Test
  public void textToImage1(){
    String prompt = "一间有着精致窗户的花店，漂亮的木质门，摆放着花朵,里面坐着一个女人";
    ImageSynthesisParam param =
        ImageSynthesisParam.builder()
            .apiKey(getApiKey())
            .model("wanx2.1-t2i-plus")
            .prompt(prompt)
            .n(1)
            .size("1024*1024")
            .build();
    ImageSynthesis imageSynthesis = new ImageSynthesis();
    ImageSynthesisResult result = null;
    try {
      result = imageSynthesis.call(param);
    } catch (ApiException | NoApiKeyException e){
      throw new RuntimeException(e.getMessage());
    }
    log.info("result:{}",result);
//    log.info(JsonUtils.toJson(result));
  }

  @Test
  public void textToImage2(){
    String string = textGenerateImageTool.generateImage(
        "一间有着精致窗户的花店，漂亮的木质门，摆放着花朵,里面坐着一个女人");
    log.info(string);
  }
  /**
   * 把文档加载到向量存储中
   */
  private void loadDocument(EmbeddingStore<TextSegment> embeddingStore){
    List<Document> documents = FileSystemDocumentLoader.loadDocuments("src/main/resources/document");
    //使用 EmbeddingModel 转换文本为向量，然后存储到自动注入的内存 embeddingStore 中
    EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
        .embeddingModel(embeddingModel)
        .embeddingStore(embeddingStore)
        .documentSplitter(documentSplitter)
        .build();
    ingestor.ingest(documents);
    EmbeddingStoreIngestor.ingest(documents, embeddingStore);
  }

  /*
   * 基于内存的向量存储
   * */
  @Test
  public void inMemoryRag(){
    loadDocument(inMemoryEmbeddingStore);
    AiManualService assistant = AiServices.builder(AiManualService.class)
        .streamingChatModel(streamingChatModel)
        .chatModel(chatModel)
        .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
        .contentRetriever(inMemoryContentRetriever)
        .chatMemoryProvider(chatMemoryProvider)
        .build();
    String res = assistant.chatStr("1","你是谁？");
    log.info(res);
  }

}
