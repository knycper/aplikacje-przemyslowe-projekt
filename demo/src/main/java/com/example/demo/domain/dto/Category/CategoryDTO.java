package com.example.demo.domain.dto.Category;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CategoryDTO {
    private UUID id;

    @NotBlank
    private String name;

    @NotBlank
    private String color;
}
