package com.example.demo.service;

import com.example.demo.domain.dto.Task.TaskDTO;
import com.example.demo.domain.dto.Task.TaskResponseDTO;
import com.example.demo.domain.entity.Category;
import com.example.demo.domain.entity.Status;
import com.example.demo.domain.entity.Task;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.exceptions.NotFoundException;
import com.example.demo.domain.mapper.TaskMapper;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
                    .filter(t -> t.getCategory().getId().equals(categoryId))
                    .toList();
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

    public TaskResponseDTO addTask(TaskDTO taskDTO) {
        User user = userRepository.findById(userService.getLoggedUserId()).orElseThrow(() -> new NotFoundException("User not found"));

        Category category = categoryRepository.findById(taskDTO.getCategory().getId())
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
}
