package com.example.todo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@Entity
@Table(name = "sys_user")
public class User {

    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "native")
    private Long id;

    /** 登录账号（唯一） */
    @Column(name = "username", unique = true, nullable = false, length = 64)
    private String username;

    /** 密码（明文存储，仅演示，生产请使用 BCrypt） */
    @Column(name = "password", nullable = false, length = 128)
    private String password;

    /** 昵称 */
    @Column(name = "nickname", length = 64)
    private String nickname;

    /** 邮箱（可选） */
    @Column(name = "email", length = 128)
    private String email;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
