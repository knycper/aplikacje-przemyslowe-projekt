package com.example.demo.domain.mapper;

import com.example.demo.domain.dto.Category.CategoryDTO;
import com.example.demo.domain.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDTO mapToDTO(Category category);
}
