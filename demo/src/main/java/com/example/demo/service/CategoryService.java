package com.example.demo.service;

import com.example.demo.domain.dto.Category.CategoryDTO;
import com.example.demo.domain.dto.Category.CategoryReceiveDTO;
import com.example.demo.domain.entity.Category;
import com.example.demo.domain.exceptions.NotFoundException;
import com.example.demo.domain.mapper.CategoryMapper;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.dao.CategoryDao;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Validated
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final TaskRepository taskRepository;
    private final CategoryDao categoryDao;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper, TaskRepository taskRepository, CategoryDao categoryDao) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.taskRepository = taskRepository;
        this.categoryDao = categoryDao;
    }

    public List<CategoryDTO> getCategories() {
        return categoryRepository.findAll().stream().map(categoryMapper::mapToDTO).collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(@NotNull UUID id) {
        Category category = this.categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Chosen category not found"));
        return categoryMapper.mapToDTO(category);
    }

    public CategoryDTO addCategory(@Valid CategoryReceiveDTO categoryDTO) {
        UUID id = UUID.randomUUID();
        categoryDao.insert(id, categoryDTO.getName(), categoryDTO.getColor());

        CategoryDTO newCategory = new CategoryDTO();
        newCategory.setId(id);
        newCategory.setName(categoryDTO.getName());
        newCategory.setColor(categoryDTO.getColor());

        return newCategory;
    }

    public CategoryDTO updateCategory(@Valid CategoryReceiveDTO categoryDTO, @NotNull UUID id) {

        int updated = categoryDao.update(id, categoryDTO.getName(), categoryDTO.getColor());

        if (updated == 0) {
            throw new NotFoundException("Category not found");
        }

        CategoryDTO updatedCategory = new CategoryDTO();
        updatedCategory.setId(id);
        updatedCategory.setName(categoryDTO.getName());
        updatedCategory.setColor(categoryDTO.getColor());

        return updatedCategory;
    }

    @Transactional
    public void deleteCategory(@NotNull UUID id) {
        taskRepository.clearCategory(id);

        int deleted = categoryDao.delete(id);

        if (deleted == 0) {
            throw new NotFoundException("Category not found");
        }
    }
}
