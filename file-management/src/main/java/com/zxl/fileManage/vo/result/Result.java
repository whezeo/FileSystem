package com.zxl.fileManage.vo.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 返回json数据给浏览器
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result implements Serializable {
    //是否成功
    private boolean succeed;
    //附加信息
    private String message;
    //状态码
    private int code;
    //返回数据
    private Object data;
    public static Result success(Object data){
        return new Result(true,"success",200,data);
    }
    public static Result fail(int code , String msg){
        return new Result(false,msg,code,null);
    }
}
