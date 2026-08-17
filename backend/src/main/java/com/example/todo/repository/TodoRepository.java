package com.example.todo.repository;

import com.example.todo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Todo Repository
 * 所有查询基于 userId 做数据隔离
 */
@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    /**
     * 查询某用户的全部待办，按创建时间倒序
     */
    List<Todo> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 根据 id + userId 查询（确保数据归属正确）
     */
    Optional<Todo> findByIdAndUserId(Long id, Long userId);
}
