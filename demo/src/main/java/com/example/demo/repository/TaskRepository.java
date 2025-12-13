package com.example.demo.repository;

import com.example.demo.domain.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    @Modifying
    @Query("UPDATE Task t SET t.category = null where t.category.id = :categoryId")
    void clearCategory(@Param("categoryId") UUID categoryId);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId")
    List<Task> findByUserId(@Param("userId") UUID userId);
}
