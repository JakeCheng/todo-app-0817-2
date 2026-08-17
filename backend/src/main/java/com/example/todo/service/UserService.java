package com.example.todo.service;

import com.example.todo.dto.LoginRequest;
import com.example.todo.dto.LoginResponse;
import com.example.todo.dto.RegisterRequest;
import com.example.todo.dto.UpdatePasswordRequest;
import com.example.todo.dto.UpdateProfileRequest;
import com.example.todo.dto.UserInfoResponse;
import com.example.todo.entity.User;
import com.example.todo.exception.BusinessException;
import com.example.todo.repository.UserRepository;
import com.example.todo.util.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 注册
     * 注册成功后直接返回 token（前端免再调登录接口）
     *
     * @param req 注册请求
     * @return 登录响应（含 token）
     */
    @Transactional
    public LoginResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new BusinessException(400, "用户名已被占用");
        }
        User user = new User();
        user.setUsername(req.getUsername());
        // 演示项目明文存储密码，生产请使用 BCrypt
        user.setPassword(req.getPassword());
        user.setNickname(req.getNickname() == null || req.getNickname().isEmpty()
                ? req.getUsername() : req.getNickname());
        user.setEmail(req.getEmail());
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        userRepository.save(user);

        return buildLoginResponse(user);
    }

    /**
     * 登录
     */
    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new BusinessException(400, "用户名或密码错误"));
        // 明文校验，生产请使用 BCrypt.matches
        if (!user.getPassword().equals(req.getPassword())) {
            throw new BusinessException(400, "用户名或密码错误");
        }
        return buildLoginResponse(user);
    }

    /**
     * 修改密码（忘记密码场景）
     * 需要传入用户名 + 原始密码 + 新密码
     */
    @Transactional
    public void updatePassword(UpdatePasswordRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new BusinessException(400, "用户不存在"));
        if (!user.getPassword().equals(req.getOldPassword())) {
            throw new BusinessException(400, "原始密码不正确");
        }
        if (user.getPassword().equals(req.getNewPassword())) {
            throw new BusinessException(400, "新密码不能与原密码相同");
        }
        user.setPassword(req.getNewPassword());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * 获取当前登录用户信息
     */
    public UserInfoResponse getCurrentUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        UserInfoResponse resp = new UserInfoResponse();
        BeanUtils.copyProperties(user, resp);
        return resp;
    }

    /**
     * 修改个人信息
     */
    @Transactional
    public UserInfoResponse updateProfile(Long userId, UpdateProfileRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        if (req.getNickname() != null) {
            user.setNickname(req.getNickname());
        }
        if (req.getEmail() != null) {
            user.setEmail(req.getEmail());
        }
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        UserInfoResponse resp = new UserInfoResponse();
        BeanUtils.copyProperties(user, resp);
        return resp;
    }

    /**
     * 构造登录响应
     */
    private LoginResponse buildLoginResponse(User user) {
        String token = jwtUtils.generateToken(user.getId(), user.getUsername());
        LoginResponse resp = new LoginResponse();
        resp.setToken(token);
        LoginResponse.UserInfoResponse info = new LoginResponse.UserInfoResponse();
        info.setId(user.getId());
        info.setUsername(user.getUsername());
        info.setNickname(user.getNickname());
        info.setEmail(user.getEmail());
        info.setCreatedAt(user.getCreatedAt());
        resp.setUser(info);
        return resp;
    }
}
