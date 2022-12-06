package com.zxl.fileManage.config;


import com.zxl.fileManage.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMmvConfig  implements WebMvcConfigurer {
    @Autowired
    LoginInterceptor loginInterceptor;
    //做跨域配置，为什么要做这个跨域的配置呢，因为比如：我前端的端口号是8080，而我后端接口是8888
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //addMapping就是所有的文件，allowedOrigins指的是可以那个地址可以访问
        registry.addMapping("/**").allowedOrigins("http://127.0.0.1:2333");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/user/changePassword")
                .addPathPatterns("/fs/**").excludePathPatterns("/fs/download/*");
    }
}
