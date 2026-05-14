package com.qingqiu.openchat.util;
import static com.qingqiu.openchat.constant.Common.REGISTER_CAPTCHA_TTL;
import static com.qingqiu.openchat.constant.Common.USER_REGISTER_CAPTCHA_KEY;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.concurrent.ThreadLocalRandom;
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
    String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    sendCode(email, code);
    return code;
  }

  public void sendCode(String email, String code) {
    log.info("开始发送邮件...获取的到邮件发送对象为:{}", mailSender);

    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
    try {
      helper.setSubject("OpenChat-邮箱验证");
      helper.setText("您收到了来自【OpenChat】发送的验证码<br><br>" +
          "有效期: "+ REGISTER_CAPTCHA_TTL +" 分钟<br><br>" +
          "验证码: " + code +"<br>" +
          "<h5>若非本人操作，请忽略本邮件</h5>", true);
      helper.setFrom(fromEmail);
      helper.setTo(email);
    } catch (MessagingException e) {
      e.printStackTrace();
    }
    log.info("发送邮箱验证码给用户：{}成功 : {}", email, code);
    log.info("MimeMessageHelper：" + helper);
    mailSender.send(mimeMessage);
  }

}
