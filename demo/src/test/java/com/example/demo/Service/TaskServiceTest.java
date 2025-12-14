package com.example.demo.Service;

import com.example.demo.domain.dto.Task.TaskReceiveDTO;
import com.example.demo.domain.dto.Task.TaskResponseDTO;
import com.example.demo.domain.dto.Task.TasksDashboard;
import com.example.demo.domain.entity.Category;
import com.example.demo.domain.entity.Task;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.enums.DeadlineFilter;
import com.example.demo.domain.enums.Status;
import com.example.demo.domain.exceptions.NotFoundException;
import com.example.demo.domain.mapper.TaskMapper;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.dao.DashboardJdbcDao;
import com.example.demo.service.TaskService;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    TaskRepository taskRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    TaskMapper taskMapper;

    @Mock
    UserService userService;

    @Mock
    DashboardJdbcDao dashboardJdbcDao;

    @InjectMocks
    TaskService taskService;

    @Test
    void shouldReturnTaskById() {
        UUID id = UUID.randomUUID();
        Task task = new Task();
        TaskResponseDTO dto = new TaskResponseDTO();

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskMapper.mapToResponseDTO(task)).thenReturn(dto);

        TaskResponseDTO result = taskService.getTaskById(id);

        assertNotNull(result);
        verify(taskRepository).findById(id);
        verify(taskMapper).mapToResponseDTO(task);
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        UUID id = UUID.randomUUID();
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taskService.getTaskById(id));
    }

    @Test
    void shouldAddTaskSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        TaskReceiveDTO dto = new TaskReceiveDTO();
        dto.setTitle("Task");
        dto.setDescription("Desc");
        dto.setStatus(Status.TODO);
        dto.setCategoryId(categoryId);
        dto.setDueDate(LocalDateTime.now());

        User user = new User();
        Category category = new Category();
        TaskResponseDTO responseDTO = new TaskResponseDTO();

        when(userService.getLoggedUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(taskMapper.mapToResponseDTO(any(Task.class))).thenReturn(responseDTO);

        TaskResponseDTO result = taskService.addTask(dto);

        assertNotNull(result);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldThrowWhenCategoryNotFound() {
        UUID userId = UUID.randomUUID();
        TaskReceiveDTO dto = new TaskReceiveDTO();
        dto.setCategoryId(UUID.randomUUID());

        when(userService.getLoggedUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(categoryRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taskService.addTask(dto));
    }

    @Test
    void shouldDeleteTask() {
        UUID id = UUID.randomUUID();
        Task task = new Task();

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        taskService.deleteTask(id);

        verify(taskRepository).delete(task);
    }

    @Test
    void shouldUpdateTaskStatus() {
        UUID id = UUID.randomUUID();
        Task task = new Task();
        TaskResponseDTO dto = new TaskResponseDTO();

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskMapper.mapToResponseDTO(task)).thenReturn(dto);

        TaskResponseDTO result = taskService.updateStatus(Status.DONE, id);

        assertNotNull(result);
        assertEquals(Status.DONE, task.getStatus());
        verify(taskRepository).save(task);
    }

    @Test
    void shouldExportTasksToCsv() {
        UUID userId = UUID.randomUUID();
        Task task = new Task();
        task.setId(UUID.randomUUID());
        task.setTitle("Test");
        task.setDescription("Desc");
        task.setStatus(Status.TODO);
        task.setDueDate(LocalDateTime.now());
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        when(userService.getLoggedUserId()).thenReturn(userId);
        when(taskRepository.findByUserId(userId)).thenReturn(List.of(task));

        byte[] result = taskService.exportTasksToCsv();

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void shouldReturnEmptyDashboardWhenNoTasks() {
        UUID userId = UUID.randomUUID();

        when(userService.getLoggedUserId()).thenReturn(userId);
        when(dashboardJdbcDao.countAll(userId)).thenReturn(0L);

        TasksDashboard dashboard = taskService.getTasksDashboard();

        assertEquals(0, dashboard.getTogether());
    }

    @Test
    void shouldReturnDashboardWithStats() {
        UUID userId = UUID.randomUUID();

        when(userService.getLoggedUserId()).thenReturn(userId);
        when(dashboardJdbcDao.countAll(userId)).thenReturn(10L);
        when(dashboardJdbcDao.countByStatus(userId, Status.TODO)).thenReturn(2L);
        when(dashboardJdbcDao.countByStatus(userId, Status.IN_PROGRESS)).thenReturn(3L);
        when(dashboardJdbcDao.countByStatus(userId, Status.DONE)).thenReturn(5L);

        TasksDashboard dashboard = taskService.getTasksDashboard();

        assertEquals(10, dashboard.getTogether());
        assertEquals(5, dashboard.getDONE());
        assertEquals(50f, dashboard.getCompletedProcent());
    }

    @Test
    void shouldGetTasksWithFilters() {
        UUID userId = UUID.randomUUID();
        Task task = new Task();
        Page<Task> page = new PageImpl<>(List.of(task));
        TaskResponseDTO dto = new TaskResponseDTO();

        when(userService.getLoggedUserId()).thenReturn(userId);
        when(taskRepository.findFiltered(
                eq(userId),
                eq("test"),
                eq(Status.TODO),
                any(),
                any(),
                any(),
                any()
        )).thenReturn(page);

        when(taskMapper.mapToResponseDTO(task)).thenReturn(dto);

        Page<TaskResponseDTO> result = taskService.getTasks(
                "test",
                Status.TODO,
                null,
                null,
                Pageable.unpaged()
        );

        assertEquals(1, result.getContent().size());
    }

    @Test
    void shouldUpdateTaskWithCategory() {
        UUID taskId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        Task task = new Task();
        Category category = new Category();
        TaskReceiveDTO dto = new TaskReceiveDTO();
        dto.setTitle("Updated");
        dto.setCategoryId(categoryId);
        dto.setStatus(Status.IN_PROGRESS);
        dto.setDueDate(LocalDateTime.now());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(taskMapper.mapToResponseDTO(task)).thenReturn(new TaskResponseDTO());

        TaskResponseDTO result = taskService.updateTask(dto, taskId);

        assertNotNull(result);
        assertEquals(category, task.getCategory());
    }

    @Test
    void shouldUpdateTaskAndClearCategory() {
        UUID taskId = UUID.randomUUID();

        Task task = new Task();
        task.setCategory(new Category());

        TaskReceiveDTO dto = new TaskReceiveDTO();
        dto.setCategoryId(null);
        dto.setStatus(Status.TODO);
        dto.setDueDate(LocalDateTime.now());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.mapToResponseDTO(task)).thenReturn(new TaskResponseDTO());

        taskService.updateTask(dto, taskId);

        assertNull(task.getCategory());
    }

    @Test
    void shouldThrowWhenUpdatingNonExistingTask() {
        UUID id = UUID.randomUUID();

        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> taskService.updateTask(new TaskReceiveDTO(), id));
    }

    @Test
    void shouldThrowWhenDeletingNonExistingTask() {
        UUID id = UUID.randomUUID();
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> taskService.deleteTask(id));
    }

    @Test
    void shouldThrowCsvIOException() {
        TaskService spy = spy(taskService);

        when(userService.getLoggedUserId()).thenReturn(UUID.randomUUID());
        when(taskRepository.findByUserId(any()))
                .thenThrow(new RuntimeException("IO error"));

        assertThrows(RuntimeException.class,
                spy::exportTasksToCsv);
    }

    @Test
    void shouldGetTasksBeforeDeadline() {
        UUID userId = UUID.randomUUID();
        Task task = new Task();
        TaskResponseDTO dto = new TaskResponseDTO();

        when(userService.getLoggedUserId()).thenReturn(userId);
        when(taskRepository.findFiltered(
                eq(userId),
                isNull(),
                isNull(),
                isNull(),
                any(),
                isNull(),
                any()
        )).thenReturn(new PageImpl<>(List.of(task)));

        when(taskMapper.mapToResponseDTO(task)).thenReturn(dto);

        Page<TaskResponseDTO> result = taskService.getTasks(
                null, null, null, DeadlineFilter.BEFORE_DEADLINE, Pageable.unpaged()
        );

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldGetTasksAfterDeadline() {
        UUID userId = UUID.randomUUID();
        Task task = new Task();
        TaskResponseDTO dto = new TaskResponseDTO();

        when(userService.getLoggedUserId()).thenReturn(userId);
        when(taskRepository.findFiltered(
                eq(userId),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any(),
                any()
        )).thenReturn(new PageImpl<>(List.of(task)));

        when(taskMapper.mapToResponseDTO(task)).thenReturn(dto);

        Page<TaskResponseDTO> result = taskService.getTasks(
                null, null, null, DeadlineFilter.AFTER_DEADLINE, Pageable.unpaged()
        );

        assertEquals(1, result.getTotalElements());
    }

}
