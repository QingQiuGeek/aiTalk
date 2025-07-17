package com.qingqiu.aigeek.util;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * 邮箱工具类
 */
@Slf4j
public class MailUtil {

  //邮件发送器
  private final JavaMailSenderImpl mailSender;
  private final String fromEmail;

  public MailUtil(JavaMailSenderImpl mailSender, String fromEmail) {
    this.mailSender = mailSender;
    this.fromEmail = fromEmail;
  }

  public String sendCode(String email) {
    int code = (int) ((Math.random() * 9 + 1) * 100000);
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
    try {
      helper.setSubject("无问青秋-邮箱注册验证");
      helper.setText("您收到了来自【无问青秋博客】发送的验证码<br>" +
          "有效期仅1分钟<br>" +
          "验证码: <span style='color :#13c2c2,font-weight: bolder,font-size: medium'>" + code
          + "</span><br>" +
          "<h5>若非本人操作，请忽略本邮件</h5>", true);
      helper.setFrom(fromEmail);
      helper.setTo(email);
    } catch (MessagingException e) {
      e.printStackTrace();
    }
    log.info("mimeMessage对象为:" + mimeMessage);
    mailSender.send(mimeMessage);
    return String.valueOf(code);
  }

}
