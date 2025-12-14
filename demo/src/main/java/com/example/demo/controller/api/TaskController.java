package com.example.demo.controller.api;

import com.example.demo.domain.dto.Task.TaskReceiveDTO;
import com.example.demo.domain.dto.Task.TaskResponseDTO;
import com.example.demo.domain.dto.Task.TasksDashboard;
import com.example.demo.domain.enums.DeadlineFilter;
import com.example.demo.domain.enums.Status;
import com.example.demo.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Tag(
        name = "Tasks",
        description = "Operations related to tasks"
)
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
})
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(
            summary = "Get tasks",
            description = "Returns a list of tasks for the logged-in user. "
                    + "Tasks can be optionally filtered by status and category."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponseDTO.class))),
    })
    @GetMapping
    public ResponseEntity<Page<TaskResponseDTO>> getTasks(
            @RequestParam(required = false) @Schema(description = "Search by title") String title,
            @RequestParam(required = false) @Schema(description = "Filter tasks by status", example = "TODO") Status status,
            @RequestParam(required = false) @Schema(description = "Filter tasks by category ID") UUID categoryId,
            @RequestParam(required = false) @Schema(description = "Filter tasks by before or after deadline") DeadlineFilter deadlineFilter,
            @Schema(description = "Paging and sorting") @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(taskService.getTasks(title, status, categoryId, deadlineFilter, pageable));
    }


    @Operation(
            summary = "Create a new task",
            description = "Creates a new task assigned to the logged-in user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody @Schema(description = "Task data to create") TaskReceiveDTO task) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.addTask(task));
    }

    @Operation(
            summary = "Get task by ID",
            description = "Returns details of a single task by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTask(@PathVariable @Schema(description = "Task ID") UUID id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @Operation(
            summary = "Update task",
            description = "Updates an existing task."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable @Schema(description = "Task ID") UUID id, @Valid @RequestBody @Schema(description = "Updated task data") TaskReceiveDTO task) {
        return ResponseEntity.ok(taskService.updateTask(task, id));
    }

    @Operation(
            summary = "Delete task",
            description = "Deletes a task by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable @Schema(description = "Task ID") UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Export tasks to CSV",
            description = "Exports all tasks of the logged-in user as a CSV file."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CSV file generated successfully",
                    content = @Content(mediaType = "text/csv")),
            @ApiResponse(responseCode = "500", description = "Csv IOException thrown while making csv file", content = @Content)
    })
    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportCsv() {
        byte[] csvBytes = taskService.exportTasksToCsv();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tasks.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(csvBytes);
    }

    @Operation(
            summary = "Get tasks dashboard",
            description = "Returns task statistics including total count, status counts and completion percentage."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dashboard data retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TasksDashboard.class)))
    })
    @GetMapping("/dashboard")
    public ResponseEntity<TasksDashboard> getDashboardTasks() {
        return ResponseEntity.ok(taskService.getTasksDashboard());
    }

    @Operation(
            summary = "Update task status",
            description = "Updates the status of a task."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task status updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    @PutMapping("/{id}/{newStatus}")
    public ResponseEntity<TaskResponseDTO> updateStatus(@PathVariable UUID id, @PathVariable Status newStatus) {
        return ResponseEntity.ok(taskService.updateStatus(newStatus, id));
    }
}
