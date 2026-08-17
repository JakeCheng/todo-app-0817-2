package com.example.todo.controller;

import com.example.todo.dto.LoginRequest;
import com.example.todo.dto.LoginResponse;
import com.example.todo.dto.RegisterRequest;
import com.example.todo.dto.UpdatePasswordRequest;
import com.example.todo.dto.UpdateProfileRequest;
import com.example.todo.dto.UserInfoResponse;
import com.example.todo.service.UserService;
import com.example.todo.util.Result;
import com.example.todo.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 用户 Controller
 *
 * 接口前缀：/api/user
 */
@CrossOrigin
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserContext userContext;

    /**
     * 注册
     * 注册成功后直接返回 token，前端可免登录直接进入主页
     */
    @PostMapping("/register")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest req) {
        return Result.success("注册成功", userService.register(req));
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return Result.success("登录成功", userService.login(req));
    }

    /**
     * 忘记密码 / 修改密码
     * 需要传入用户名 + 原始密码 + 新密码
     */
    @PostMapping("/password")
    public Result<Void> updatePassword(@Valid @RequestBody UpdatePasswordRequest req) {
        userService.updatePassword(req);
        return Result.success("密码修改成功", null);
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public Result<UserInfoResponse> info() {
        return Result.success(userService.getCurrentUserInfo(userContext.getCurrentUserId()));
    }

    /**
     * 修改个人信息
     */
    @PutMapping("/info")
    public Result<UserInfoResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest req) {
        return Result.success("更新成功", userService.updateProfile(userContext.getCurrentUserId(), req));
    }
}
