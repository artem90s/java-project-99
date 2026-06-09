package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.TaskStatusDtoForUpdate;
import hexlet.code.model.TaskStatus;

import java.util.List;

public interface TaskStatusService {
    TaskStatus getStatusById(Long id);

    List<TaskStatus> getStatuses();

    TaskStatus createStatus(TaskStatusDto dto);

    TaskStatus updateStatus(Long id, TaskStatusDtoForUpdate dto);

    void deleteStatus(Long id);
}
