package com.example.demo.Service;

import com.example.demo.domain.dto.Category.CategoryDTO;
import com.example.demo.domain.dto.Category.CategoryReceiveDTO;
import com.example.demo.domain.entity.Category;
import com.example.demo.domain.exceptions.NotFoundException;
import com.example.demo.domain.mapper.CategoryMapper;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.dao.CategoryDao;
import com.example.demo.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    CategoryMapper categoryMapper;

    @Mock
    TaskRepository taskRepository;

    @Mock
    CategoryDao categoryDao;

    @InjectMocks
    CategoryService categoryService;

    @Test
    void shouldReturnAllCategories() {
        Category category = new Category();
        CategoryDTO dto = new CategoryDTO();

        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(categoryMapper.mapToDTO(category)).thenReturn(dto);

        List<CategoryDTO> result = categoryService.getCategories();

        assertEquals(1, result.size());
        verify(categoryRepository).findAll();
        verify(categoryMapper).mapToDTO(category);
    }

    @Test
    void shouldReturnCategoryById() {
        UUID id = UUID.randomUUID();
        Category category = new Category();
        CategoryDTO dto = new CategoryDTO();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryMapper.mapToDTO(category)).thenReturn(dto);

        CategoryDTO result = categoryService.getCategoryById(id);

        assertNotNull(result);
        verify(categoryRepository).findById(id);
        verify(categoryMapper).mapToDTO(category);
    }

    @Test
    void shouldThrowWhenCategoryNotFoundById() {
        UUID id = UUID.randomUUID();

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> categoryService.getCategoryById(id));
    }

    @Test
    void shouldAddCategory() {
        CategoryReceiveDTO dto = new CategoryReceiveDTO();
        dto.setName("Home");
        dto.setColor("#000000");

        CategoryDTO result = categoryService.addCategory(dto);

        assertNotNull(result.getId());
        assertEquals("Home", result.getName());
        assertEquals("#000000", result.getColor());

        verify(categoryDao).insert(any(UUID.class), eq("Home"), eq("#000000"));
    }

    @Test
    void shouldUpdateCategory() {
        UUID id = UUID.randomUUID();
        CategoryReceiveDTO dto = new CategoryReceiveDTO();
        dto.setName("Updated");
        dto.setColor("#AAAAAA");

        when(categoryDao.update(id, "Updated", "#AAAAAA")).thenReturn(1);

        CategoryDTO result = categoryService.updateCategory(dto, id);

        assertEquals(id, result.getId());
        assertEquals("Updated", result.getName());
        assertEquals("#AAAAAA", result.getColor());
    }

    @Test
    void shouldThrowWhenUpdatingNonExistingCategory() {
        UUID id = UUID.randomUUID();
        CategoryReceiveDTO dto = new CategoryReceiveDTO();
        dto.setName("Updated");
        dto.setColor("#AAAAAA");

        when(categoryDao.update(id, "Updated", "#AAAAAA")).thenReturn(0);

        assertThrows(NotFoundException.class,
                () -> categoryService.updateCategory(dto, id));
    }

    @Test
    void shouldDeleteCategoryAndClearTasks() {
        UUID id = UUID.randomUUID();

        when(categoryDao.delete(id)).thenReturn(1);

        categoryService.deleteCategory(id);

        verify(categoryDao).delete(id);
        verify(taskRepository).clearCategory(id);
    }

    @Test
    void shouldThrowWhenDeletingNonExistingCategory() {
        UUID id = UUID.randomUUID();

        when(categoryDao.delete(id)).thenReturn(0);

        assertThrows(NotFoundException.class,
                () -> categoryService.deleteCategory(id));

        verify(taskRepository, never()).clearCategory(any());
    }
}
