package com.qingqiu.aigeek.util;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;
/**
 * @author: QingQiu
 * @date: 2025/7/16 16:40
 * @description:
 */

public class ImageUtil {

  /**
   * 读取 resources/static/images/demo.png 并返回 MultipartFile
   * 路径以 classpath 为根目录
   */
//  public static MultipartFile readImageAsMultipartFile(String classpathPath) throws IOException {
//    // classpathPath 形如 "static/images/demo.png"
//
//    try (InputStream in = new ClassPathResource(classpathPath).getInputStream()) {
//      if (in == null) {
//        throw new IllegalArgumentException("文件不存在: " + classpathPath);
//      }
//      // 取文件名
//      String fileName = classpathPath.substring(classpathPath.lastIndexOf('/') + 1);
//      // 取 contentType（简单处理）
//      String contentType = "image/" + fileName.substring(fileName.lastIndexOf('.') + 1);
//      return new MockMultipartFile(
//          "file",
//          fileName,
//          contentType,
//          in
//      );
//    }
//  }
}
