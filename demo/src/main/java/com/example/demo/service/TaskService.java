package com.example.demo.service;

import com.example.demo.domain.dto.Task.TaskDTO;
import com.example.demo.domain.dto.Task.TaskReceiveDTO;
import com.example.demo.domain.dto.Task.TaskResponseDTO;
import com.example.demo.domain.dto.Task.TasksDashboard;
import com.example.demo.domain.entity.Category;
import com.example.demo.domain.entity.Status;
import com.example.demo.domain.entity.Task;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.exceptions.CsvIOException;
import com.example.demo.domain.exceptions.NotFoundException;
import com.example.demo.domain.mapper.TaskMapper;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.opencsv.CSVWriter;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TaskMapper taskMapper;
    private final UserService userService;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository, CategoryRepository categoryRepository, TaskMapper taskMapper, UserService userService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.taskMapper = taskMapper;
        this.userService = userService;
    }

    public List<TaskResponseDTO> getFilteredTasks(Status status, UUID categoryId) {
        List<Task> tasks = taskRepository.findAll().stream()
                .filter(t -> t.getUser().getId().equals(userService.getLoggedUserId()))
                .toList();

        if (status != null) {
            tasks = tasks.stream()
                    .filter(t -> t.getStatus() == status)
                    .toList();
        }

        if (categoryId != null) {
            tasks = tasks.stream()
                    .filter(t -> t.getCategory() != null && t.getCategory().getId().equals(categoryId)).toList();
        }

        return tasks.stream()
                .map(taskMapper::mapToResponseDTO)
                .toList();

    }

    public TaskResponseDTO getTaskById(UUID guid) {
        Task task = taskRepository.findById(guid)
                .orElseThrow(() -> new NotFoundException("Chosen task not found"));

        return taskMapper.mapToResponseDTO(task);
    }

    public TaskResponseDTO addTask(TaskReceiveDTO taskDTO) {
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
    public TaskResponseDTO updateTask(TaskReceiveDTO taskDTO, UUID id) {
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

    public void deleteTask(UUID id) {
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
        List<Task> tasks = taskRepository.findByUserId(userService.getLoggedUserId());

        int totalTasks = tasks.size();

        if (totalTasks == 0) {
            return new TasksDashboard();
        }

        long todo = tasks.stream().filter(task -> task.getStatus() == Status.TODO).count();

        long inProgress = tasks.stream().filter(task -> task.getStatus() == Status.IN_PROGRESS).count();

        long done = tasks.stream().filter(task -> task.getStatus() == Status.DONE).count();

        float completedProcent = (done * 100f) / totalTasks;

        TasksDashboard tasksDashboard = new TasksDashboard();
        tasksDashboard.setTogether(totalTasks);
        tasksDashboard.setTODO(todo);
        tasksDashboard.setIN_PROGRESS(inProgress);
        tasksDashboard.setDONE(done);
        tasksDashboard.setCompletedProcent(completedProcent);

        return tasksDashboard;
    }

    public TaskResponseDTO updateStatus(Status newStatus, UUID taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new NotFoundException("Chosen task not found"));

        task.setStatus(newStatus);
        taskRepository.save(task);

        return taskMapper.mapToResponseDTO(task);
    }
}
