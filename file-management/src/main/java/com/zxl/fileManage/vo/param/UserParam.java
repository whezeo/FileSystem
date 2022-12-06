package com.zxl.fileManage.vo.param;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserParam {
    private String newPassword;
    private String email;
    private String verification;
    private String password;
}
