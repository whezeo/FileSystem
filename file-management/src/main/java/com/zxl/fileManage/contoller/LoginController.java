package com.zxl.fileManage.contoller;


import com.zxl.fileManage.pojo.User;
import com.zxl.fileManage.service.UserService;
import com.zxl.fileManage.vo.param.UserParam;
import com.zxl.fileManage.vo.result.Result;
import com.zxl.fileManage.vo.utils.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class LoginController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody User user){
        return userService.userLogin(user);
    }

    @PostMapping("/logout")
    public Result logout(){
        return userService.userLogOut();
    }
    @PostMapping("changePassword")
    public Result changePassword( @RequestBody UserParam userParam){
        return userService.changePwd(userParam.getNewPassword());
    }
    @PostMapping("resetPassword")
    public Result resetPassword( @RequestBody UserParam userParam){
        return userService.resetPwd(userParam);
    }
    @PostMapping("sendVerification")
    public Result sendVerification( @RequestBody UserParam userParam){
        return userService.sendVerification(userParam.getEmail());
    }
    @PostMapping("register")
    public Result register( @RequestBody UserParam userParam){
        return userService.register(userParam);
    }
}
