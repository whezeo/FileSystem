package com.zxl.fileManage.vo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zxl.fileManage.pojo.User;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTUtils {
    private static final String jwtToken = "123456";
    private static final String tokenHead="Bearer ";

    public static String createToken(User user){
        Map<String,Object> claims = new HashMap<>();
        claims.put("user",user);
        JwtBuilder jwtBuilder = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, jwtToken) // 签发算法，秘钥为jwtToken
                .setClaims(claims) // body数据，要唯一，自行设置
                .setIssuedAt(new Date()) // 设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + 10 * 60 * 60 * 1000));// 10个小时有效时间
        String token = jwtBuilder.compact();
        return token;
    }

    public static Map<String, Object> checkToken(String token){

        token = token.substring(tokenHead.length());
        try {
            Jwt parse = Jwts.parser().setSigningKey(jwtToken).parse(token);
            return (Map<String, Object>) parse.getBody();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        User asd = new User(1l, "45", "asd");
        String token = createToken(asd);
        System.out.println(token);
        Map<String, Object> map = checkToken(token);
        Object user = map.get("user");
        User user1 = objectMapper.convertValue(user, User.class);
        System.out.println(user);
        Long id = user1.getId();
        System.out.println(id);
    }
}


