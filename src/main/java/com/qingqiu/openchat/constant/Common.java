package com.qingqiu.openchat.constant;


/**
 * @author: 懒大王Smile
 * @date: 2024/9/12 23:08
 * @description: 通用常量字符串
 */
public interface Common {

  String SA_TOKEN_USER_ROLE = "sa-token:role:";

  //会话聊天历史存储key
  String CHAT_HISTORY = "chat:history:";

  //会话聊天历史存储时间
  Integer CHAT_HISTORY_TTL = 60;

  //文件保存目录
  String FILE_SAVE_DIR = System.getProperty("user.dir") + "/tmp";

  String HOT_IP_KEY="hotIp";

  String IMG_UPLOAD_DIR = "/uploadImg/";

  //限制可上传的图片大小
  int IMG_SIZE_LIMIT = 1;

  //限制可上传的图片大小单位
  String IMG_SIZE_UNIT = "M";

  //注册验证码有效期 1min
  Long REGISTER_CAPTCHA_TTL = 1L;

  String USER_REGISTER_CAPTCHA_KEY = "Blog:user:registerCaptcha:";

  //邮箱正则
  String EMAIL_REGEX = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";

  //https://goregex.cn/
  //用户名匹配，仅支持中文、英文、数字、下划线，长度2-6
  String USERNAME_REGEX = "^[\\u4E00-\\u9FA5A-Za-z0-9_]{2,6}$";

  //必须包含字母和数字，不能使用特殊字符
  String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z0-9]{6,10}$";

}
