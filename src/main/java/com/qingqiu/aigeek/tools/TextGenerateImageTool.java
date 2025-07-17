package com.qingqiu.aigeek.tools;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam.ImageSynthesisParamBuilder;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.utils.JsonUtils;
import com.qingqiu.aigeek.config.QwenModelConfig;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author: QingQiu
 * @date: 2025/7/15 20:38
 * @description:
 */
@Slf4j
@Component
public class TextGenerateImageTool {

  @Resource
  private ImageSynthesisParamBuilder iSynthesisParamBuilder;

  /**
   * 根据用户描述生成图片
   *
   * @param prompt 用户文本描述，如“画一只可爱的小狗”
   * @return 成功返回图片 URL；失败返回错误提示
   */
  @Tool(value = {
      "generate_image",
      "当你需要**根据文字描述生成图片、画等**时调用此工具。",
      "入参：用户文字描述；返回：https格式的图片URL，如https://xxxx。"
  })
  public String generateImage(
      @P(value = "用户有关图片的文字描述，例如：一只在草地上奔跑的蔡徐坤") String prompt) {

    if (prompt == null || prompt.isBlank()) {
      return "描述为空，无法生成图片!";
    }

    try {
      ImageSynthesis conv = new ImageSynthesis();
      ImageSynthesisParam param = iSynthesisParamBuilder.prompt(prompt).build();
      ImageSynthesisResult result = conv.call(param);
      log.info("文生图结果:{}", JsonUtils.toJson(result));

      // SUCCEEDED 取第一张图 URL
      if ("SUCCEEDED".equalsIgnoreCase(result.getOutput().getTaskStatus())) {
        return result.getOutput().getResults()
            .getFirst().get("url");
      } else {
        return "图片生成任务未完成,状态: " + result.getOutput().getTaskStatus();
      }

    } catch (Exception e) {
      log.error("调用文生图模型失败: ", e);
      return "抱歉，生成图片时出现异常: " + e.getMessage();
    }
  }

}
