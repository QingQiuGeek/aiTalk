package com.qingqiu.aigeek.tools;

import com.qingqiu.aigeek.enums.BusinessExceptionEnum;
import com.qingqiu.aigeek.exception.BusinessException;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.V;
import java.io.IOException;
import java.util.Base64;
import java.util.Set;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: QingQiu
 * @date: 2025/7/16 16:20
 * @description:
 */
@Configuration
public class Base64Tool {

  /** 允许的图片后缀集合，可按需扩展 */
  private static final Set<String> IMG_EXT = Set.of(
      "png", "jpg", "jpeg", "gif", "bmp", "webp"
  );

  /**
   * 将上传的图片文件转成带 data URI 的 Base64 字符串
   * @param image 待转换的图片文件
   * @return 形如 data:image/png;base64,xxxxx...
   * @throws IOException 读取文件失败
   */
  @Tool("Convert an uploaded image file to a data-URI base64 string")
  public String imageConvertToBase64(@V("The image file to be converted") MultipartFile image) throws IOException {

    if (image == null || image.isEmpty()) {
      throw new BusinessException(BusinessExceptionEnum.PARAMS_ERROR.getCode(), BusinessExceptionEnum.PARAMS_ERROR.getMessage());
    }
    // 1. 取后缀并校验
    String original = image.getOriginalFilename();
    if (original == null || !original.contains(".")) {
      throw new BusinessException(BusinessExceptionEnum.IMAGE_TYPE_ERROR.getCode(), BusinessExceptionEnum.IMAGE_TYPE_ERROR.getMessage());
    }
    String ext = original.substring(original.lastIndexOf('.') + 1);
    if (!IMG_EXT.contains(ext)) {
      throw new BusinessException(BusinessExceptionEnum.IMAGE_TYPE_ERROR.getCode(), BusinessExceptionEnum.IMAGE_TYPE_ERROR.getMessage());
    }
    // 2. 编码
    String base64 = Base64.getEncoder().encodeToString(image.getBytes());
    // 3. 拼接前缀
    return "data:image/" + ext + ";base64," + base64;
  }


}
