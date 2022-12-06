package com.zxl.fileManage.interceptor;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zxl.fileManage.pojo.User;
import com.zxl.fileManage.service.UserService;
import com.zxl.fileManage.vo.result.ErrorCode;
import com.zxl.fileManage.vo.result.Result;
import com.zxl.fileManage.vo.utils.JWTUtils;
import com.zxl.fileManage.vo.utils.UserThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /**
         * 1.需要判断,请求的接口路径是否为HandleMethod(controller方法)
         * 2.判断token是否为空,为空未登录
         * 3.不为空,登录验证 loginService  checkToken
         * 4.如果认证成功放行
         **/
        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        String token = request.getHeader("authorization");
        //日志问题,需要导入lombok下的@slf4
        log.info("=============request start=================");
        String requestURI = request.getRequestURI();
        log.info("request uri:{}", requestURI);
        log.info("request method:{}", request.getMethod());
        log.info("token:{}", token);
        log.info("=============request end===================");
        //为空返回ERR_NOT_LOGIN
        if(StringUtils.isBlank(token)){
            Result result = Result.fail(ErrorCode.ERR_NOT_LOGIN.getCode(), ErrorCode.ERR_NOT_LOGIN.getMsg());
            response.setContentType("application/json;charset=utf8");
            response.getWriter().print(JSON.toJSONString(result));
        }
        System.out.println(token);
        User user =  userService.checkToken(token);
        if(user==null){
            Result result = Result.fail(ErrorCode.ERR_TOKEN_INVALID.getCode(), ErrorCode.ERR_TOKEN_INVALID.getMsg());
            response.setContentType("application/json;charset=utf8");
            response.getWriter().print(JSON.toJSONString(result));
        }
        //将用户信息放入localThread
        UserThreadLocal.put(user);
        return true;
    }
}
