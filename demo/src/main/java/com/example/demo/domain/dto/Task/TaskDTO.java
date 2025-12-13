package com.example.demo.domain.dto.Task;

import com.example.demo.domain.dto.Category.CategoryDTO;
import com.example.demo.domain.entity.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class TaskDTO {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private Status status;

    @NotBlank
    private CategoryDTO category;

    @NotBlank
    private LocalDateTime dueDate;

    @NotBlank
    private LocalDateTime createdAt;

    @NotBlank
    private LocalDateTime updatedAt;
}
