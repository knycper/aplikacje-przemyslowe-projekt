package com.example.demo.Repository;

import com.example.demo.domain.entity.Category;
import com.example.demo.domain.entity.Task;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.enums.Status;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
class TaskRepositoryTest {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CategoryRepository categoryRepository;

    private User saveUser() {
        User user = new User();
        user.setUsername("user_" + UUID.randomUUID());
        user.setPassword("pass");
        return userRepository.save(user);
    }

    private Category saveCategory() {
        Category category = new Category();
        category.setName("Work");
        category.setColor("#FFF");
        return categoryRepository.save(category);
    }

    private Task saveTask(User user, Category category, Status status, String title) {
        Task task = new Task();
        task.setTitle(title);
        task.setStatus(status);
        task.setUser(user);
        task.setCategory(category);
        task.setDueDate(LocalDateTime.now().plusDays(1));
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    @Test
    void shouldSaveTask() {
        User user = saveUser();
        Category category = saveCategory();

        Task task = saveTask(user, category, Status.TODO, "Task 1");

        assertThat(task.getId()).isNotNull();
    }

    @Test
    void shouldFindTaskById() {
        User user = saveUser();
        Task task = saveTask(user, null, Status.TODO, "Task");

        Task found = taskRepository.findById(task.getId()).orElse(null);

        assertThat(found).isNotNull();
    }

    @Test
    void shouldDeleteTask() {
        User user = saveUser();
        Task task = saveTask(user, null, Status.TODO, "Delete");

        taskRepository.delete(task);

        assertThat(taskRepository.findById(task.getId())).isEmpty();
    }

    @Test
    void shouldUpdateTaskStatus() {
        User user = saveUser();
        Task task = saveTask(user, null, Status.TODO, "Update");

        task.setStatus(Status.DONE);
        taskRepository.save(task);

        Task updated = taskRepository.findById(task.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(Status.DONE);
    }

    @Test
    void shouldFindTasksByUserId() {
        User user = saveUser();
        saveTask(user, null, Status.TODO, "A");
        saveTask(user, null, Status.DONE, "B");

        List<Task> tasks = taskRepository.findByUserId(user.getId());

        assertThat(tasks).hasSize(2);
    }

    @Test
    void shouldReturnEmptyListForUserWithoutTasks() {
        User user = saveUser();

        List<Task> tasks = taskRepository.findByUserId(user.getId());

        assertThat(tasks).isEmpty();
    }

    @Test
    void shouldClearCategoryFromTasks() {
        User user = saveUser();
        Category category = saveCategory();
        Task task = saveTask(user, category, Status.TODO, "Cat");

        taskRepository.clearCategory(category.getId());

        Task updated = taskRepository.findById(task.getId()).orElseThrow();
        assertThat(updated.getCategory()).isNull();
    }

    @Test
    void shouldFilterByTitle() {
        User user = saveUser();
        saveTask(user, null, Status.TODO, "Important task");
        saveTask(user, null, Status.TODO, "Other");

        Page<Task> page = taskRepository.findFiltered(
                user.getId(),
                "important",
                null,
                null,
                null,
                null,
                PageRequest.of(0, 10)
        );

        assertThat(page.getContent()).hasSize(1);
    }

    @Test
    void shouldFilterByStatus() {
        User user = saveUser();
        saveTask(user, null, Status.TODO, "A");
        saveTask(user, null, Status.DONE, "B");

        Page<Task> page = taskRepository.findFiltered(
                user.getId(),
                null,
                Status.DONE,
                null,
                null,
                null,
                PageRequest.of(0, 10)
        );

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getStatus()).isEqualTo(Status.DONE);
    }

    @Test
    void shouldFilterByCategory() {
        User user = saveUser();
        Category category = saveCategory();
        saveTask(user, category, Status.TODO, "A");
        saveTask(user, null, Status.TODO, "B");

        Page<Task> page = taskRepository.findFiltered(
                user.getId(),
                null,
                null,
                category.getId(),
                null,
                null,
                PageRequest.of(0, 10)
        );

        assertThat(page.getContent()).hasSize(1);
    }

    @Test
    void shouldFilterByBeforeDeadline() {
        User user = saveUser();
        Task task = saveTask(user, null, Status.TODO, "Deadline");
        task.setDueDate(LocalDateTime.now().minusDays(1));
        taskRepository.save(task);

        Page<Task> page = taskRepository.findFiltered(
                user.getId(),
                null,
                null,
                null,
                LocalDateTime.now(),
                null,
                PageRequest.of(0, 10)
        );

        assertThat(page.getContent()).hasSize(1);
    }
}
