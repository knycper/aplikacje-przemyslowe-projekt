package com.example.demo.domain.dto.Task;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TasksDashboard {
    private int together = 0;

    private long TODO = 0L;

    private long IN_PROGRESS = 0L;

    private long DONE = 0L;

    private float CompletedProcent = 0f;
}
