package com.example.todo.controller;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.service.TodoService;
import com.example.todo.util.Result;
import com.example.todo.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * Todo Controller
 *
 * 接口前缀：/api/todo
 * 全部需要 token 鉴权
 */
@CrossOrigin
@RestController
@RequestMapping("/api/todo")
public class TodoController {

    @Autowired
    private TodoService todoService;

    @Autowired
    private UserContext userContext;

    /**
     * 查询当前用户全部待办
     */
    @GetMapping
    public Result<List<TodoResponse>> list() {
        return Result.success(todoService.list(userContext.getCurrentUserId()));
    }

    /**
     * 新增待办
     */
    @PostMapping
    public Result<TodoResponse> create(@Valid @RequestBody TodoRequest req) {
        return Result.success("新增成功", todoService.create(userContext.getCurrentUserId(), req));
    }

    /**
     * 修改待办（标题、完成状态）
     */
    @PutMapping("/{id}")
    public Result<TodoResponse> update(@PathVariable Long id, @Valid @RequestBody TodoRequest req) {
        return Result.success("更新成功", todoService.update(userContext.getCurrentUserId(), id, req));
    }

    /**
     * 切换完成状态
     */
    @PutMapping("/{id}/toggle")
    public Result<TodoResponse> toggle(@PathVariable Long id) {
        return Result.success(todoService.toggleCompleted(userContext.getCurrentUserId(), id));
    }

    /**
     * 删除待办
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        todoService.delete(userContext.getCurrentUserId(), id);
        return Result.success("删除成功", null);
    }
}
