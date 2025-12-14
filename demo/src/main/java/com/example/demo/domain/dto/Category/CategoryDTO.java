package com.example.demo.domain.dto.Category;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CategoryDTO {
    @NotBlank(message = "ID must not be blank")
    private UUID id;

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotBlank(message = "Color mus not be blank")
    private String color;
}
