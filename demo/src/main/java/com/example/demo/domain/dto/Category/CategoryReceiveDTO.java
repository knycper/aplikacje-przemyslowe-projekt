package com.example.demo.domain.dto.Category;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryReceiveDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String color;
}
