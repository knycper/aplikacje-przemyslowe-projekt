package com.example.demo.repository.dao;

import com.example.demo.domain.entity.Category;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class CategoryDao {
    private final JdbcTemplate jdbcTemplate;

    public CategoryDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Category> findAll() {
        String sql = "SELECT id, name, color FROM category";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Category c = new Category();
            c.setId(UUID.fromString(rs.getString("id")));
            c.setName(rs.getString("name"));
            c.setColor(rs.getString("color"));
            return c;
        });
    }

    // baza sama nie wygeneruje uuid wiec musze sam
    public void insert(UUID id, String name, String color) {
        String sql = """
            INSERT INTO category (id, name, color)
            VALUES (?, ?, ?)
        """;

        jdbcTemplate.update(sql, id, name, color);
    }

    public int update(UUID id, String name, String color) {
        String sql = """
            UPDATE category
            SET name = ?, color = ?
            WHERE id = ?
        """;

        return jdbcTemplate.update(sql, name, color, id);
    }

    public int delete(UUID id) {
        String sql = "DELETE FROM category WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
