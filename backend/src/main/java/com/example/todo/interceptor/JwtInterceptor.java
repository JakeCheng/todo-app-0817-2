package com.example.todo.interceptor;

import com.example.todo.exception.BusinessException;
import com.example.todo.util.JwtUtils;
import com.example.todo.util.UserContext;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT 拦截器
 * 校验 token，并将 userId 写入 request 上下文
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String header = request.getHeader("Authorization");
        if (header == null || header.isEmpty()) {
            throw new BusinessException(401, "未登录或 token 缺失");
        }

        // 兼容 "Bearer xxx" 与 "xxx" 两种形式
        String token = header;
        if (header.startsWith("Bearer ")) {
            token = header.substring(7);
        }

        if (!jwtUtils.validateToken(token)) {
            throw new BusinessException(401, "token 无效或已过期，请重新登录");
        }

        try {
            Claims claims = jwtUtils.parseToken(token);
            Object userIdObj = claims.get("userId");
            Long userId;
            // JWT 数字默认解析为 Integer，需要兼容转换
            if (userIdObj instanceof Integer) {
                userId = ((Integer) userIdObj).longValue();
            } else {
                userId = (Long) userIdObj;
            }
            request.setAttribute(UserContext.CURRENT_USER_ID, userId);
        } catch (Exception e) {
            throw new BusinessException(401, "token 解析失败，请重新登录");
        }
        return true;
    }
}
