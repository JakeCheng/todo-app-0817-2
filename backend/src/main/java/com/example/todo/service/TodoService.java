package com.example.todo.service;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.entity.Todo;
import com.example.todo.exception.BusinessException;
import com.example.todo.repository.TodoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Todo 服务
 * 全部基于当前登录 userId 做数据隔离
 */
@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    /**
     * 查询当前用户的全部待办
     */
    public List<TodoResponse> list(Long userId) {
        List<Todo> todos = todoRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return todos.stream().map(this::toResponse).collect(Collectors.toList());
    }

    /**
     * 新增待办
     */
    @Transactional
    public TodoResponse create(Long userId, TodoRequest req) {
        Todo todo = new Todo();
        todo.setUserId(userId);
        todo.setTitle(req.getTitle());
        todo.setCompleted(req.getCompleted() != null && req.getCompleted());
        todo.setCreatedAt(LocalDateTime.now());
        todoRepository.save(todo);
        return toResponse(todo);
    }

    /**
     * 修改待办（标题、完成状态）
     */
    @Transactional
    public TodoResponse update(Long userId, Long id, TodoRequest req) {
        Todo todo = todoRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(404, "待办不存在或无权限"));
        if (req.getTitle() != null) {
            todo.setTitle(req.getTitle());
        }
        if (req.getCompleted() != null) {
            todo.setCompleted(req.getCompleted());
        }
        todoRepository.save(todo);
        return toResponse(todo);
    }

    /**
     * 切换完成状态
     */
    @Transactional
    public TodoResponse toggleCompleted(Long userId, Long id) {
        Todo todo = todoRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(404, "待办不存在或无权限"));
        todo.setCompleted(!todo.getCompleted());
        todoRepository.save(todo);
        return toResponse(todo);
    }

    /**
     * 删除待办
     */
    @Transactional
    public void delete(Long userId, Long id) {
        Todo todo = todoRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(404, "待办不存在或无权限"));
        todoRepository.delete(todo);
    }

    /**
     * 实体转响应 DTO
     */
    private TodoResponse toResponse(Todo todo) {
        TodoResponse resp = new TodoResponse();
        BeanUtils.copyProperties(todo, resp);
        return resp;
    }
}
