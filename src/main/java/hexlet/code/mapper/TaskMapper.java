package hexlet.code.mapper;

import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskResponse;
import hexlet.code.dto.TaskUpdateDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "status", source = "taskStatus.slug")
    @Mapping(target = "title", source = "name")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "taskLabelIds", source = "labels", qualifiedByName = "labelsToIds")
    TaskResponse toDto(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "labels", ignore = true)
    Task toEntity(TaskDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "labels", ignore = true)
    Task toEntity(TaskUpdateDto dto);

    @Named("labelsToIds")
    default List<Long> labelsToIds(List<Label> labels) {
        if (labels == null) {
            return new ArrayList<>();
        }
        return labels.stream()
                .map(Label::getId)
                .collect(Collectors.toList());
    }
}
