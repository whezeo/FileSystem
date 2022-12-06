package com.zxl.fileManage.vo.utils;


import com.zxl.fileManage.pojo.User;

public class UserThreadLocal {
    //构造器私有化
    private UserThreadLocal(){}

    //实例化一个ThreadLocal的类，也就是启用
    private static final ThreadLocal<User> LOCAL = new ThreadLocal<>();
    public static void put(User sysUser){
        LOCAL.set(sysUser);
    }

    public static User get(){
        return LOCAL.get();
    }

    public static void remove() {
        LOCAL.remove();
    }
}
