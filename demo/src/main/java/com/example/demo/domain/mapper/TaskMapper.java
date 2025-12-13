package com.example.demo.domain.mapper;

import com.example.demo.domain.dto.Task.TaskDTO;
import com.example.demo.domain.dto.Task.TaskResponseDTO;
import com.example.demo.domain.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface TaskMapper {
    TaskDTO mapToDTO(Task task);

    @Mapping(source = "user.id", target = "userId")
    TaskResponseDTO mapToResponseDTO(Task task);
}
