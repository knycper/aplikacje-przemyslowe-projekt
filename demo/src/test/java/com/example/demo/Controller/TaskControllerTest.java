package com.example.demo.Controller;

import com.example.demo.controller.api.TaskController;
import com.example.demo.domain.dto.Task.TaskReceiveDTO;
import com.example.demo.domain.dto.Task.TaskResponseDTO;
import com.example.demo.domain.dto.Task.TasksDashboard;
import com.example.demo.domain.enums.Status;
import com.example.demo.domain.exceptions.GlobalExceptionHandler;
import com.example.demo.domain.exceptions.NotFoundException;
import com.example.demo.security.SecurityConfig;
import com.example.demo.security.service.CustomUserDetailsService;
import com.example.demo.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;


    private TaskResponseDTO sampleResponse() {
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(UUID.randomUUID());
        dto.setTitle("Test task");
        dto.setDescription("Test description");
        dto.setStatus(Status.TODO);
        dto.setDueDate(LocalDateTime.now().plusDays(5));
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        return dto;
    }

    private TaskReceiveDTO sampleRequest() {
        TaskReceiveDTO dto = new TaskReceiveDTO();
        dto.setTitle("Test task");
        dto.setDescription("Test description");
        dto.setStatus(Status.TODO);
        dto.setDueDate(LocalDateTime.now().plusDays(5));
        dto.setCategoryId(null);
        return dto;
    }


    @Test
    @WithMockUser
    void shouldReturnTasksPage() throws Exception {
        Page<TaskResponseDTO> page =
                new PageImpl<>(List.of(sampleResponse()));

        when(taskService.getTasks(
                any(), any(), any(), any(), any(Pageable.class)
        )).thenReturn(page);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test task"));
    }

    @Test
    @WithMockUser
    void shouldReturnTaskById() throws Exception {
        UUID id = UUID.randomUUID();

        when(taskService.getTaskById(id))
                .thenReturn(sampleResponse());

        mockMvc.perform(get("/api/tasks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test task"));
    }

    @Test
    @WithMockUser
    void shouldReturn404WhenTaskNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        when(taskService.getTaskById(id))
                .thenThrow(new NotFoundException("Task not found"));

        mockMvc.perform(get("/api/tasks/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Task not found"));
    }

    @Test
    @WithMockUser
    void shouldCreateTask() throws Exception {
        when(taskService.addTask(any(TaskReceiveDTO.class)))
                .thenReturn(sampleResponse());

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test task"));
    }

    @Test
    @WithMockUser
    void shouldReturn400WhenValidationFails() throws Exception {
        String invalidJson = """
                {
                  "title": "",
                  "description": "",
                  "status": null,
                  "dueDate": "2020-01-01T10:00:00"
                }
                """;

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.status").exists());
    }

    @Test
    @WithMockUser
    void shouldDeleteTask() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(taskService).deleteTask(id);

        mockMvc.perform(delete("/api/tasks/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void shouldExportCsv() throws Exception {
        when(taskService.exportTasksToCsv())
                .thenReturn("a,b,c".getBytes());

        mockMvc.perform(get("/api/tasks/export/csv"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=tasks.csv"
                ));
    }

    @Test
    @WithMockUser
    void shouldReturnDashboard() throws Exception {
        TasksDashboard dashboard = new TasksDashboard();
        dashboard.setTogether(10);
        dashboard.setDONE(5);
        dashboard.setTODO(0);
        dashboard.setIN_PROGRESS(0);
        dashboard.setCompletedProcent(0.0f);

        when(taskService.getTasksDashboard()).thenReturn(dashboard);

        mockMvc.perform(get("/api/tasks/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.together").value(10))
                .andExpect(jsonPath("$.done").value(5))
                .andExpect(jsonPath("$.todo").value(0))
                .andExpect(jsonPath("$.in_PROGRESS").value(0))
                .andExpect(jsonPath("$.completedProcent").value(0.0));
    }

    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }
}
