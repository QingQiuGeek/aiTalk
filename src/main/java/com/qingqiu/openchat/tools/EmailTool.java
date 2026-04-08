package com.qingqiu.openchat.tools;

import com.qingqiu.openchat.util.MailUtil;
import dev.langchain4j.agent.tool.Tool;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class EmailTool implements ITool {

    @Resource
    private JavaMailSenderImpl mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public String getName() {
        return "emailTool";
    }

    @Override
    public String getDescription() {
        return "Send email asynchronously";
    }

    @Override
    public ToolType getType() {
        return ToolType.OPTIONAL;
    }

    @Tool(name = "sendEmail", value = "Send email with to, subject, content")
    public String sendEmail(String to, String subject, String content) {
        if (!StringUtils.hasText(to) || !StringUtils.hasText(subject) || !StringUtils.hasText(content)) {
            return "Error: to, subject and content are required.";
        }
        MailUtil mailUtil = new MailUtil(mailSender, fromEmail);
        return mailUtil.sendCode(to);
    }
}
