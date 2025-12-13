package com.example.demo.domain.dto.Task;

import com.example.demo.domain.dto.Category.CategoryDTO;
import com.example.demo.domain.entity.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TaskReceiveDTO {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private Status status;

    @NotBlank
    private CategoryDTO category;
}
