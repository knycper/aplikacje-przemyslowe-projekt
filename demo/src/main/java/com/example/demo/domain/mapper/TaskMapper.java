package com.example.demo.domain.mapper;

import com.example.demo.domain.dto.Task.TaskDTO;
import com.example.demo.domain.dto.Task.TaskResponseDTO;
import com.example.demo.domain.entity.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface TaskMapper {
    TaskDTO mapToDTO(Task task);

    TaskResponseDTO mapToResponseDTO(Task task);
}
