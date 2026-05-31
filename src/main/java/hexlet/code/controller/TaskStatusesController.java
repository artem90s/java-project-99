package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.TaskStatusDtoForUpdate;
import hexlet.code.dto.TaskStatusResponse;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.service.TaskStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/task_statuses")
@RequiredArgsConstructor
public final class TaskStatusesController {
    private final TaskStatusService service;
    private final TaskStatusMapper mapper;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatus getStatus(@PathVariable Long id) {
        return service.getStatusById(id);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<TaskStatusResponse>> getStatuses() {
        var statuses = service.getStatuses().stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok().header("X-Total-Count", String.valueOf(statuses.size())).body(statuses);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatusResponse createStatus(@RequestBody @Valid TaskStatusDto dto) {
        return mapper.toResponse(service.createStatus(dto));
    }
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusResponse updateStatus(@PathVariable Long id, @RequestBody @Valid TaskStatusDtoForUpdate dto) {
        return mapper.toResponse(service.updateStatus(id, dto));
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.deleteStatus(id);
    }
}
