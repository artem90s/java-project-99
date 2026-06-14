package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskParams;
import hexlet.code.dto.TaskResponse;
import hexlet.code.dto.TaskUpdateDto;

import java.util.List;

public interface TaskService {
    TaskResponse getTaskById(Long id);

    List<TaskResponse> getAllTasks(TaskParams params);

    TaskResponse createTask(TaskDto dto);

    TaskResponse updateTask(Long id, TaskUpdateDto dto);

    void deleteTask(Long id);
}
