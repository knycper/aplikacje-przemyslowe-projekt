package com.example.demo.Controller;

import com.example.demo.controller.api.CategoryController;
import com.example.demo.domain.dto.Category.CategoryDTO;
import com.example.demo.domain.dto.Category.CategoryReceiveDTO;
import com.example.demo.domain.exceptions.NotFoundException;
import com.example.demo.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void shouldReturnAllCategories() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(UUID.randomUUID());
        dto.setName("Work");
        dto.setColor("#FFFFFF");

        when(categoryService.getCategories()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Work"));
    }

    @Test
    @WithMockUser
    void shouldCreateCategory() throws Exception {
        CategoryReceiveDTO receiveDTO = new CategoryReceiveDTO();
        receiveDTO.setName("Home");
        receiveDTO.setColor("#000000");

        CategoryDTO responseDTO = new CategoryDTO();
        responseDTO.setId(UUID.randomUUID());
        responseDTO.setName("Home");
        responseDTO.setColor("#000000");

        when(categoryService.addCategory(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(receiveDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Home"));
    }


    @Test
    @WithMockUser
    void shouldReturnCategoryById() throws Exception {
        UUID id = UUID.randomUUID();

        CategoryDTO dto = new CategoryDTO();
        dto.setId(id);
        dto.setName("Work");
        dto.setColor("#123456");

        when(categoryService.getCategoryById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/categories/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Work"));
    }

    @Test
    @WithMockUser
    void shouldReturn404WhenCategoryNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        when(categoryService.getCategoryById(id))
                .thenThrow(new NotFoundException("Category not found"));

        mockMvc.perform(get("/api/categories/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void shouldUpdateCategory() throws Exception {
        UUID id = UUID.randomUUID();

        CategoryReceiveDTO receiveDTO = new CategoryReceiveDTO();
        receiveDTO.setName("Updated");
        receiveDTO.setColor("#AAAAAA");

        CategoryDTO responseDTO = new CategoryDTO();
        responseDTO.setId(id);
        responseDTO.setName("Updated");
        responseDTO.setColor("#AAAAAA");

        when(categoryService.updateCategory(any(), eq(id))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/categories/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(receiveDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }


    @Test
    @WithMockUser
    void shouldDeleteCategory() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(categoryService).deleteCategory(id);

        mockMvc.perform(delete("/api/categories/{id}", id)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }


    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isUnauthorized());
    }
}
