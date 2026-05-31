package hexlet.code.mapper;

import hexlet.code.dto.TaskStatusResponse;
import hexlet.code.model.TaskStatus;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskStatusMapper {
    TaskStatusResponse toResponse(TaskStatus status);
}

