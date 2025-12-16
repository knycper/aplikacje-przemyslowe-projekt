package com.example.demo.Repository.dao;

import com.example.demo.domain.entity.Category;
import com.example.demo.repository.dao.CategoryDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(CategoryDao.class)
class CategoryDaoTest {

    @Autowired
    private CategoryDao categoryDao;

    @Test
    void shouldReturnAllInitialCategories() {
        List<Category> categories = categoryDao.findAll();

        assertThat(categories).hasSize(9);
        assertThat(categories)
                .extracting(Category::getName)
                .containsExactlyInAnyOrder(
                        "Work", "Home", "Learning", "Health", "Finances",
                        "Shopping", "Projects", "Important", "Hobby"
                );
    }

    @Test
    void shouldUpdateCategory() {
        Category work = categoryDao.findAll().stream()
                .filter(c -> c.getName().equals("Work"))
                .findFirst()
                .orElseThrow();

        int updated = categoryDao.update(
                work.getId(),
                "Work Updated",
                "#000000"
        );

        assertThat(updated).isEqualTo(1);

        Category updatedCategory = categoryDao.findAll().stream()
                .filter(c -> c.getId().equals(work.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(updatedCategory.getName()).isEqualTo("Work Updated");
        assertThat(updatedCategory.getColor()).isEqualTo("#000000");
    }

    @Test
    void shouldDeleteCategory() {
        Category hobby = categoryDao.findAll().stream()
                .filter(c -> c.getName().equals("Hobby"))
                .findFirst()
                .orElseThrow();

        int initialSize = categoryDao.findAll().size();

        int deleted = categoryDao.delete(hobby.getId());

        assertThat(deleted).isEqualTo(1);
        assertThat(categoryDao.findAll()).hasSize(initialSize - 1);
    }

    @Test
    void shouldReturnZeroWhenDeletingNonExistingCategory() {
        UUID id = UUID.randomUUID();

        int deleted = categoryDao.delete(id);

        assertThat(deleted).isEqualTo(0);
    }

}
