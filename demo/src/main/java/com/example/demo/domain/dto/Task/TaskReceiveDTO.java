package com.example.demo.domain.dto.Task;

import com.example.demo.domain.enums.Status;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class TaskReceiveDTO {

    @NotBlank(message = "Title must not be blank")
    private String title;

    @NotBlank(message = "Description must not be blank")
    private String description;

    @NotNull(message = "Status must not be null")
    private Status status;

    @Nullable
    private UUID categoryId;

    @NotNull(message = "Due date must not be null")
    @Future(message = "Due date must be in the future")
    private LocalDateTime dueDate;
}
