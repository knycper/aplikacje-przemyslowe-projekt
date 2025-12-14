package com.example.demo.service;

import com.example.demo.domain.dto.Task.TaskReceiveDTO;
import com.example.demo.domain.dto.Task.TaskResponseDTO;
import com.example.demo.domain.dto.Task.TasksDashboard;
import com.example.demo.domain.entity.Category;
import com.example.demo.domain.enums.DeadlineFilter;
import com.example.demo.domain.enums.Status;
import com.example.demo.domain.entity.Task;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.enums.TaskSort;
import com.example.demo.domain.exceptions.CsvIOException;
import com.example.demo.domain.exceptions.NotFoundException;
import com.example.demo.domain.mapper.TaskMapper;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.dao.DashboardJdbcDao;
import com.opencsv.CSVWriter;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Validated
@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TaskMapper taskMapper;
    private final UserService userService;
    private final DashboardJdbcDao dashboardJdbcDao;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository, CategoryRepository categoryRepository, TaskMapper taskMapper, UserService userService, DashboardJdbcDao dashboardJdbcDao) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.taskMapper = taskMapper;
        this.userService = userService;
        this.dashboardJdbcDao = dashboardJdbcDao;
    }

    public Page<TaskResponseDTO> getTasks(String title, Status status, UUID categoryId, DeadlineFilter deadlineFilter, Pageable pageable) {
        LocalDateTime before = null;
        LocalDateTime after = null;

        if (deadlineFilter == DeadlineFilter.BEFORE_DEADLINE) {
            before = LocalDateTime.now();
        } else if (deadlineFilter == DeadlineFilter.AFTER_DEADLINE) {
            after = LocalDateTime.now();
        }

        Page<Task> tasks = taskRepository.findFiltered(userService.getLoggedUserId(), title, status, categoryId, before, after, pageable );

        return tasks.map(taskMapper::mapToResponseDTO);

    }

    public TaskResponseDTO getTaskById(@NotNull UUID guid) {
        Task task = taskRepository.findById(guid)
                .orElseThrow(() -> new NotFoundException("Chosen task not found"));

        return taskMapper.mapToResponseDTO(task);
    }

    public TaskResponseDTO addTask(@Valid TaskReceiveDTO taskDTO) {
        User user = userRepository.findById(userService.getLoggedUserId()).orElseThrow(() -> new NotFoundException("User not found"));

        Category category = categoryRepository.findById(taskDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Chosen category not found"));

        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        task.setCategory(category);
        task.setDueDate(taskDTO.getDueDate());
        task.setUser(user);

        taskRepository.save(task);

        return taskMapper.mapToResponseDTO(task);
    }

    @Transactional
    public TaskResponseDTO updateTask(@Valid TaskReceiveDTO taskDTO, @NotNull UUID id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new NotFoundException("Chosen task not found"));

        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        task.setDueDate(taskDTO.getDueDate());
        if (taskDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(taskDTO.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Chosen category not found"));
            task.setCategory(category);
        } else {
            task.setCategory(null);
        }

        return taskMapper.mapToResponseDTO(task);
    }

    public void deleteTask(@NotNull UUID id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new NotFoundException("Chosen task not found"));
        taskRepository.delete(task);
    }

    public byte[] exportTasksToCsv() {
        List<Task> tasks = taskRepository.findByUserId(userService.getLoggedUserId());

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

            CSVWriter csvWriter = new CSVWriter(writer);

            csvWriter.writeNext(new String[]{
                    "ID", "Title", "Description", "Status",
                    "Due Date", "Category", "Created At", "Updated At"
            });

            for (Task task : tasks) {
                csvWriter.writeNext(new String[]{
                        task.getId().toString(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getStatus().name(),
                        task.getDueDate().toString(),
                        task.getCategory() != null ? task.getCategory().getName() : "",
                        task.getCreatedAt().toString(),
                        task.getUpdatedAt().toString()
                });
            }

            csvWriter.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new CsvIOException("Błąd tworzenia pliku csv");
        }
    }

    public TasksDashboard getTasksDashboard() {
        UUID userid = userService.getLoggedUserId();

        long totalTasks = dashboardJdbcDao.countAll(userid);

        if (totalTasks == 0) {
            return new TasksDashboard();
        }

        long todo = dashboardJdbcDao.countByStatus(userid, Status.TODO);

        long inProgress = dashboardJdbcDao.countByStatus(userid, Status.IN_PROGRESS);

        long done = dashboardJdbcDao.countByStatus(userid, Status.DONE);

        float completedProcent = (done * 100f) / totalTasks;

        TasksDashboard tasksDashboard = new TasksDashboard();
        tasksDashboard.setTogether(totalTasks);
        tasksDashboard.setTODO(todo);
        tasksDashboard.setIN_PROGRESS(inProgress);
        tasksDashboard.setDONE(done);
        tasksDashboard.setCompletedProcent(completedProcent);

        return tasksDashboard;
    }

    public TaskResponseDTO updateStatus(@NotNull Status newStatus, @NotNull UUID taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new NotFoundException("Chosen task not found"));

        task.setStatus(newStatus);
        taskRepository.save(task);

        return taskMapper.mapToResponseDTO(task);
    }
}
