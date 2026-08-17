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
 * Todo 待办实体
 * 基于登录用户做数据隔离（通过 userId 关联）
 */
@Data
@Entity
@Table(name = "todo")
public class Todo {

    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "native")
    private Long id;

    /** 所属用户 ID（数据隔离关键） */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 标题 */
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    /** 是否完成 */
    @Column(name = "completed", nullable = false)
    private Boolean completed = false;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
