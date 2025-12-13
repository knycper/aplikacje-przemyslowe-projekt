package com.example.demo.domain.dto.Task;

import com.example.demo.domain.entity.Status;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

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

    @Nullable
    private UUID categoryId;

    @NotBlank
    private LocalDateTime dueDate;
}
