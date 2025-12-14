package com.example.demo.repository.dao;

import com.example.demo.domain.enums.Status;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class DashboardJdbcDao {

    private final JdbcTemplate jdbcTemplate;

    public DashboardJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long countAll(UUID userId) {
        Long result = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM task WHERE user_id = ?",
                Long.class,
                userId
        );

        return result != null ? result : 0L;
    }

    public long countByStatus(UUID userId, Status status) {
        Long result = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM task WHERE user_id = ? AND status = ?",
                Long.class,
                userId,
                status.name()
        );

        return result != null ? result : 0L;
    }
}
