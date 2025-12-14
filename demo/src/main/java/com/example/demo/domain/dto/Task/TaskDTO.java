package com.example.demo.domain.dto.Task;

import com.example.demo.domain.dto.Category.CategoryDTO;
import com.example.demo.domain.enums.Status;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class TaskDTO {

    @NotBlank(message = "Title must not be blank")
    @Size(max = 40, min = 3, message = "Title min length is 3 and max length is 40")
    private String title;

    @NotBlank(message = "Description must not be blank")
    private String description;

    @NotNull(message = "Status must not be null")
    private Status status;

    @NotNull(message = "Category must not be null")
    private CategoryDTO category;

    @NotNull(message = "Due date must not be null")
    @Future(message = "Due date must be a future date")
    private LocalDateTime dueDate;

    @NotNull(message = "Creation date must not be null")
    @Past(message = "Creation date must be a past date")
    private LocalDateTime createdAt;

    @NotNull(message = "Update date must not be null")
    private LocalDateTime updatedAt;
}
