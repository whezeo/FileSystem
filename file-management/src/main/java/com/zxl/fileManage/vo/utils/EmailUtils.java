package com.zxl.fileManage.vo.utils;

import com.zxl.fileManage.vo.param.EmailModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
 
@Component
/*邮箱工具类*/
public class EmailUtils {
 
    @Autowired
    private JavaMailSenderImpl sender;
    @Autowired
    MailProperties mailProperties;
 
    public  void  sendEmail(EmailModel emailModel){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom(mailProperties.getProperties().get("from"));
        message.setTo(emailModel.getTo());
        message.setSubject(emailModel.getSubject());
        message.setText(emailModel.getContent());
        sender.send(message);
    }
}