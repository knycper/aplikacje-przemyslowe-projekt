package com.example.demo.service;

import com.example.demo.domain.dto.Category.CategoryDTO;
import com.example.demo.domain.dto.Category.CategoryReceiveDTO;
import com.example.demo.domain.entity.Category;
import com.example.demo.domain.exceptions.NotFoundException;
import com.example.demo.domain.mapper.CategoryMapper;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final TaskRepository taskRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper, TaskRepository taskRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.taskRepository = taskRepository;
    }

    public List<CategoryDTO> getCategories() {
        return categoryRepository.findAll().stream().map(categoryMapper::mapToDTO).collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(UUID id) {
        Category category = this.categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Chosen category not found"));
        return categoryMapper.mapToDTO(category);
    }

    public CategoryDTO addCategory(CategoryReceiveDTO categoryDTO) {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setColor(categoryDTO.getColor());
        return categoryMapper.mapToDTO(this.categoryRepository.save(category));
    }

    public CategoryDTO updateCategory(CategoryReceiveDTO categoryDTO, UUID id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Chosen category not found"));

        category.setName(categoryDTO.getName());
        category.setColor(categoryDTO.getColor());
        return categoryMapper.mapToDTO(this.categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Chosen category not found"));

        taskRepository.clearCategory(id);
        this.categoryRepository.deleteById(category.getId());
    }
}
