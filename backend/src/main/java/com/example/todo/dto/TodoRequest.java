package com.example.todo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Todo 新增/修改请求
 */
@Data
public class TodoRequest {

    @NotBlank(message = "标题不能为空")
    @Size(max = 255, message = "标题长度不能超过 255")
    private String title;

    /** 是否完成（新增时可指定，默认 false） */
    private Boolean completed = false;
}
