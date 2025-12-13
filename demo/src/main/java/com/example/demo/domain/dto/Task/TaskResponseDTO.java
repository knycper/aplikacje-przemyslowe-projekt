package com.example.demo.domain.dto.Task;

import com.example.demo.domain.dto.Category.CategoryDTO;
import com.example.demo.domain.entity.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class TaskResponseDTO {
    private UUID id;

    private String title;

    private String description;

    private Status status;

    private LocalDateTime dueDate;

    private CategoryDTO category;

    private UUID userId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
