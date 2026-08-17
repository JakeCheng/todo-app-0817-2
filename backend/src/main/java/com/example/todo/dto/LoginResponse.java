package com.example.todo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录/注册成功响应
 */
@Data
public class LoginResponse {

    /** JWT token */
    private String token;

    /** token 类型 */
    private String tokenType = "Bearer";

    /** 用户信息 */
    private UserInfoResponse user;

    @Data
    public static class UserInfoResponse {
        private Long id;
        private String username;
        private String nickname;
        private String email;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
        private LocalDateTime createdAt;
    }
}
