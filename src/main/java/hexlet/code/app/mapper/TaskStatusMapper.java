package hexlet.code.app.mapper;

import hexlet.code.app.dto.TaskStatusResponse;
import hexlet.code.app.model.TaskStatus;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskStatusMapper {
    TaskStatusResponse toResponse(TaskStatus status);
}

