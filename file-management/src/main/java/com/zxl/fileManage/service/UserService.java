package com.zxl.fileManage.service;

import com.zxl.fileManage.pojo.User;
import com.zxl.fileManage.vo.param.UserParam;
import com.zxl.fileManage.vo.result.Result;


public interface UserService {
    /**
     * 登录
     * @param user
     * @return
     */
    Result userLogin(User user);

    /**
     * 通过账号密码查找
     * @param email
     * @param password
     * @return
     */
    User findUser(String email,String password);

    /**
     * 判断token是否有效
     * @param token
     * @return
     */
    User checkToken(String token);

    /**
     * 等出
     * @return
     */
    Result userLogOut();

    /**
     * 修改密码
     * @param newPassword
     * @return
     */
    Result changePwd(String newPassword);

    /**
     * 更新密码
     * @param user
     * @return
     */
    int updatePwd(User user);

    /**
     * 重置密码
     * @param userParam
     * @return
     */
    Result resetPwd(UserParam userParam);

    /**
     * 发送邮键
     * @param email
     * @return
     */
    Result sendVerification(String email);

    /**
     * 注册
     * @param userParam
     * @return
     */
    Result register(UserParam userParam);

    /***
     * 发送新的密码
     * @param email
     * @return
     */
    String sendNewPassword(String email);

    /**
     * 验证验证码
     * @param userParam
     * @return
     */
    boolean checkVerification(UserParam userParam);

    /**
     * 通过email查找
     * @param email
     * @return
     */
    User selectUserByEmail(String email);
}
