package com.example.todo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 个人信息更新请求
 */
@Data
public class UpdateProfileRequest {

    /** 昵称 */
    @Size(max = 64, message = "昵称长度不能超过 64")
    private String nickname;

    /** 邮箱 */
    @Size(max = 128, message = "邮箱长度不能超过 128")
    private String email;
}
