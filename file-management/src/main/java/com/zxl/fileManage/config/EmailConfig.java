package com.zxl.fileManage.config;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;
 
/*邮箱配置类*/
@Configuration
@EnableConfigurationProperties(MailProperties.class)  //使MailProperties类生效
public class EmailConfig {
 
    @Autowired
    MailProperties mailProperties;
 
    @Bean
    JavaMailSenderImpl javaMailSenderImpl(){
        JavaMailSenderImpl sender=new JavaMailSenderImpl();
        sender.setHost(mailProperties.getHost());
        sender.setPort(mailProperties.getPort());
        sender.setUsername(mailProperties.getUsername());
        sender.setPassword(mailProperties.getPassword());
        return sender;
    }
}