package com.example.demo.repository;

import com.example.demo.domain.enums.DeadlineFilter;
import com.example.demo.domain.enums.Status;
import com.example.demo.domain.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    // czyscimy cache aby zaktualizowane dane byly, zapisuje zmiany do bazy przed wykonaniem zapytania
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Task t SET t.category = null where t.category.id = :categoryId")
    void clearCategory(@Param("categoryId") UUID categoryId);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId")
    List<Task> findByUserId(@Param("userId") UUID userId);

    // sprwadzanie null jest po to aby jesli nie wporwadzono to nie szukac po statusie
    @Query("""
    SELECT t FROM Task t
    WHERE t.user.id = :userId
    AND (:title IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%')))
    AND (:status IS NULL OR t.status = :status)
    AND (:categoryId IS NULL OR t.category.id = :categoryId)
    AND (:before IS NULL OR t.dueDate < :before)
    AND (:after IS NULL OR t.dueDate > :after)
""")
    Page<Task> findFiltered(
            @Param("userId") UUID userId,
            @Param("title") String title,
            @Param("status") Status status,
            @Param("categoryId") UUID categoryId,
            @Param("before") LocalDateTime before,
            @Param("after") LocalDateTime after,
            Pageable pageable
    );
}
