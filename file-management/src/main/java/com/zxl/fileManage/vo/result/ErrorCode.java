package com.zxl.fileManage.vo.result;



public enum ErrorCode {
    //若用户未登录的情况下请求特权接口，统一返回失败且 message 指定为 ERR_NOT_LOGIN
    ERR_NOT_LOGIN("ERR_NOT_LOGIN",10001),
    //若用户 token 无效的情况下请求特权接口，统一返回失败且 message 指定为 ERR_TOKEN_INVALID
    ERR_TOKEN_INVALID("ERR_TOKEN_INVALID",10002),
    ERR_USER_BLANK("邮箱和密码不能为空",10003),
    ERR_USER_NOT_FIND("邮箱和密码不正确",10004),
    ERR_VER("验证码错误或已失效",10005),
    ERR_FILE_EXIST("目标路径已存在同名文件",10006)
    ;
    private String msg;
    private int code;

    ErrorCode(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
