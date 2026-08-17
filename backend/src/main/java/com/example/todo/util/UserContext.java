package com.example.todo.util;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 当前登录用户上下文工具
 * 从 ThreadLocal / Request 中获取当前用户信息
 */
@Component
public class UserContext {

    /** 当前请求用户 ID 的请求属性 key */
    public static final String CURRENT_USER_ID = "currentUserId";

    /**
     * 获取当前登录用户 ID（由拦截器写入）
     *
     * @return 用户 ID，未登录返回 null
     */
    public Long getCurrentUserId() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        Object userId = request.getAttribute(CURRENT_USER_ID);
        if (userId == null) {
            return null;
        }
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        return (Long) userId;
    }
}
