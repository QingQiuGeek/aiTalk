package com.qingqiu.aigeek.tools;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam.MultiModalConversationParamBuilder;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.utils.JsonUtils;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author: QingQiu
 * @date: 2025/7/15 20:38
 * @description:
 */
@Slf4j
@Component
public class ImageGenerateTextTool {

  @Resource
  MultiModalConversationParamBuilder multiModalConversationParamBuilder;
  /**
   * 解析图片并回答用户问题。
   * @param base64Image 图片的 Base64 字符串（不含 data:image 头）
   * @param userMessage 用户针对图片提出的问题
   * @return 多模态模型给出的文字答案；若失败则返回错误提示
   */
  @Tool(value = {
      "analyze_image",
      "当你需要**理解画、照片等图片内容**或**回答与图片有关的问题**时调用此工具。",
      "入参：图片(Base64格式的字符串，必须包含data:image前缀) + 用户的问题；返回：模型生成的文字描述/回答。"
  })
  public String analyzeImage(
      @P(value = "图片的 Base64 字符串，必须包含data:image前缀，例如：data:image/png;base64,xxx...") String base64Image,
      @P(value = "用户针对图片提出的问题，例如：描述这张图片") String userMessage) {
    if (base64Image == null || base64Image.isBlank()) {
      return "图片为空，无法解析!";
    }
    try {
      MultiModalConversation conv = new MultiModalConversation();
      MultiModalMessage userMsg = MultiModalMessage.builder()
          .role(Role.USER.getValue())
          .content(List.of(
              Map.of("image", base64Image),
              Map.of("text",  userMessage)
          ))
          .build();
      MultiModalConversationParam param = multiModalConversationParamBuilder.message(userMsg).build();
      MultiModalConversationResult result = conv.call(param);
      log.info("图生文结果:{}", JsonUtils.toJson(result));
      return result.getOutput().getChoices().getFirst().getMessage().getContent()
          .stream()
          .filter(c -> c.containsKey("text"))
          .map(c -> c.get("text").toString())
          .findFirst()
          .orElse("模型未返回文本内容。");
    } catch (Exception e) {
      log.error("调用图生文模型失败: ", e);
      return "抱歉，解析图片时出现异常：" + e.getMessage();
    }
  }

}
