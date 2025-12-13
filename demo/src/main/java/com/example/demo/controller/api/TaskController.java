package com.example.demo.controller.api;

import com.example.demo.domain.dto.Task.TaskDTO;
import com.example.demo.domain.dto.Task.TaskResponseDTO;
import com.example.demo.domain.entity.Status;
import com.example.demo.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getTasks(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) UUID categoryId
    ) {
        return ResponseEntity.ok(taskService.getFilteredTasks(status, categoryId));
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@RequestBody TaskDTO taskDTO) {
        return ResponseEntity.ok(taskService.addTask(taskDTO));
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test");
    }
}
