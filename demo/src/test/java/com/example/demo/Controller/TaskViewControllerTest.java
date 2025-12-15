package com.example.demo.Controller;

import com.example.demo.controller.view.TaskViewController;
import com.example.demo.domain.dto.Task.TaskReceiveDTO;
import com.example.demo.domain.dto.Task.TaskResponseDTO;
import com.example.demo.domain.enums.Status;
import com.example.demo.service.CategoryService;
import com.example.demo.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.nullValue;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;

import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


@WebMvcTest(TaskViewController.class)
@WithMockUser
class TaskViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void shouldDisplayTasksPage() throws Exception {

        when(taskService.getTasks(
                isNull(), isNull(), isNull(), isNull(), eq(Pageable.unpaged())
        )).thenReturn(Page.empty());


        when(categoryService.getCategories()).thenReturn(List.of());

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("statuses"))
                .andExpect(model().attribute("selectedStatus", nullValue()))
                .andExpect(model().attribute("selectedCategory", nullValue()));
    }

    @Test
    void shouldDisplayAddTaskForm() throws Exception {

        when(categoryService.getCategories()).thenReturn(List.of());

        mockMvc.perform(get("/tasks/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("task-form"))
                .andExpect(model().attributeExists("task"))
                .andExpect(model().attribute("edit", false));
    }

    @Test
    void shouldAddTaskAndRedirect() throws Exception {

        mockMvc.perform(post("/tasks/add")
                        .with(csrf())
                        .param("title", "Test task")
                        .param("description", "Description")
                        .param("status", "TODO")
                        .param("dueDate", "2030-01-01T10:00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));

        verify(taskService).addTask(any(TaskReceiveDTO.class));
    }

    @Test
    void shouldReturnFormWhenValidationErrorsOccur() throws Exception {

        when(categoryService.getCategories()).thenReturn(List.of());

        mockMvc.perform(post("/tasks/add")
                        .with(csrf())
                        .param("title", "") // invalid
                        .param("description", "")
                        .param("status", "")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("task-form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attribute("edit", false));

        verify(taskService, never()).addTask(any());
    }

    @Test
    void shouldDisplayEditTaskForm() throws Exception {

        UUID id = UUID.randomUUID();

        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setTitle("Task");
        dto.setDescription("Desc");
        dto.setStatus(Status.TODO);
        dto.setDueDate(LocalDateTime.now().plusDays(1));

        when(taskService.getTaskById(id)).thenReturn(dto);
        when(categoryService.getCategories()).thenReturn(List.of());

        mockMvc.perform(get("/tasks/edit/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("task-form"))
                .andExpect(model().attribute("edit", true))
                .andExpect(model().attribute("taskId", id));
    }

    @Test
    void shouldUpdateTaskAndRedirect() throws Exception {

        UUID id = UUID.randomUUID();

        mockMvc.perform(post("/tasks/edit/{id}", id)
                        .with(csrf())
                        .param("title", "Updated task")
                        .param("description", "Updated desc")
                        .param("status", "DONE")
                        .param("dueDate", "2030-01-01T10:00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));

        verify(taskService).updateTask(any(TaskReceiveDTO.class), eq(id));
    }

    @Test
    void shouldReturnEditFormWhenValidationErrorsOccur() throws Exception {

        UUID id = UUID.randomUUID();

        when(categoryService.getCategories()).thenReturn(List.of());

        mockMvc.perform(post("/tasks/edit/{id}", id)
                        .with(csrf())
                        .param("title", "")
                        .param("description", "")
                        .param("status", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("task-form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attribute("edit", true));

        verify(taskService, never()).updateTask(any(), any());
    }
}
