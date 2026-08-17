package com.example.todo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Todo 响应 DTO
 */
@Data
public class TodoResponse {

    private Long id;

    private Long userId;

    private String title;

    private Boolean completed;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createdAt;
}
