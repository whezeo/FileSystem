package com.zxl.fileManage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zxl.fileManage.mapper.UserMapper;
import com.zxl.fileManage.pojo.User;
import com.zxl.fileManage.service.UserService;
import com.zxl.fileManage.vo.param.EmailModel;
import com.zxl.fileManage.vo.param.UserParam;
import com.zxl.fileManage.vo.result.ErrorCode;
import com.zxl.fileManage.vo.result.Result;
import com.zxl.fileManage.vo.utils.EmailUtils;
import com.zxl.fileManage.vo.utils.JWTUtils;
import com.zxl.fileManage.vo.utils.RedisUtil;
import com.zxl.fileManage.vo.utils.UserThreadLocal;
import net.sf.jsqlparser.expression.StringValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    UserMapper userMapper;
    @Autowired
    EmailUtils emailUtils;
    @Override
    public Result userLogin(User user) {
        String email = user.getEmail();
        String password = user.getPassword();
        if(StringUtils.isBlank(email)||StringUtils.isBlank(password)){
            return Result.fail(ErrorCode.ERR_USER_BLANK.getCode(), ErrorCode.ERR_USER_BLANK.getMsg());
        }
        User user1 = this.findUser(email, password);
        //用户不存在
        if(user1==null){
            return Result.fail(ErrorCode.ERR_USER_NOT_FIND.getCode(), ErrorCode.ERR_USER_NOT_FIND.getMsg());
        }
        String token = JWTUtils.createToken(user);
        //将token存入redis
        redisUtil.set("user_" + user1.getId(), token , 60*60*10);
        System.out.println(token);
        return Result.success(token);
    }

    @Override
    public User findUser(String email, String password) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("email",email);
        userQueryWrapper.eq("password",password);
        User user = userMapper.selectOne(userQueryWrapper);
        System.out.println(user);
        return user;
    }

    @Override
    public User checkToken(String token) {
        if(StringUtils.isBlank(token)){
            return null;
        }
        Map<String, Object> map = JWTUtils.checkToken(token);
        if(map==null){
            return null;
        }
        Object obj= map.get("user");
        ObjectMapper objectMapper = new ObjectMapper();
        User user = objectMapper.convertValue(obj, User.class);
        User user1 = this.findUser(user.getEmail(), user.getPassword());
        return user1;
    }

    @Override
    public Result userLogOut() {
        Set<String > keys = redisTemplate.keys("*");
        for (String key : keys) {
            redisTemplate.delete(key);
        }
        UserThreadLocal.remove();
        return Result.success(null);
    }

    @Override
    public Result changePwd(String newPassword) {
        User user = UserThreadLocal.get();
        this.updatePwd(user);
        user.setPassword(newPassword);
        int i = updatePwd(user);
        if(i!=1){
        return Result.fail(10008,"修改密码失败");
        }
        UserThreadLocal.remove();
        return Result.success(null);
    }

    public int updatePwd(User user) {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("email",user.getEmail());
        return userMapper.update(user,wrapper);
    }
    public boolean checkVerification(UserParam userParam){
        //前端传过来的验证码
        String verification = userParam.getVerification();
        //redis中的验证码
        String verification1 = (String)redisUtil.get(userParam.getEmail());
        if(StringUtils.isBlank(verification1)){
            return false;
        }
        if(!verification1.equals(verification)){
            return false;
        }
        return true;
    }
    @Override
    public Result resetPwd(UserParam userParam) {
        String email = userParam.getEmail();
        String verification = userParam.getVerification();
        //判断验证码是否正确
        boolean flag = this.checkVerification(userParam);
        if(!flag){
        return Result.fail(ErrorCode.ERR_VER.getCode(),ErrorCode.ERR_VER.getMsg());
        }
        //发送新的密码
        String newPassword = this.sendNewPassword(email);
        User user = new User();
        user.setPassword(newPassword);
        user.setEmail(email);
        int i = this.updatePwd(user);
        if(i!=1){
            return Result.fail(10008,"修改密码失败");
        }

        return Result.success(null);
    }

    public String sendNewPassword(String email) {
        String newPassword = "Aa12345678";
        EmailModel emailModel = new EmailModel();
        emailModel.setTo(email);
        emailModel.setSubject("新密码");
        emailModel.setContent("您的新密码为：" + newPassword);
        emailUtils.sendEmail(emailModel);
        return newPassword;
    }

    @Override
    public Result sendVerification(String email) {
        String code= String.valueOf((int)((Math.random()*9+1)*100000));
        EmailModel emailModel = new EmailModel();
        emailModel.setTo(email);
        emailModel.setSubject("验证码");
        emailModel.setContent("您的验证码为：" + code +"\n请在五分钟内使用");
        emailUtils.sendEmail(emailModel);
        //redisTemplate.opsForValue().set("email",code,5*60);
        redisUtil.set(email,code,5*60);
        //**
        System.out.println(redisUtil.get(email));
        return Result.success(null);
    }

    @Override
    public Result register(UserParam userParam) {
        /**
         * 1.check 验证码
         * 2.email 是否存在
         * 2. 写入数据库
         * 3.返回result
         */
        boolean flag = this.checkVerification(userParam);
        if(!flag){
            return Result.fail(ErrorCode.ERR_VER.getCode(), ErrorCode.ERR_VER.getMsg());
        }
        //是否存在
        User user1 = selectUserByEmail(userParam.getEmail());
        if(user1!=null){
            return Result.fail(10000, "此邮箱已被注册");
        }
        User user = new User();
        user.setEmail(userParam.getEmail());
        user.setPassword(userParam.getPassword());
        int insert = userMapper.insert(user);
        if(insert!=1){
            return Result.fail(10000, "插入失败");
        }
        return Result.success(null);
    }

    public User selectUserByEmail(String email){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email",email);
        User user = userMapper.selectOne(wrapper);
        return user;
    }
}
