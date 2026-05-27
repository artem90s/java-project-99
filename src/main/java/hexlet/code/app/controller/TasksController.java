package hexlet.code.app.controller;

import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.dto.TaskStatusDtoForUpdate;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/task_statuses")
@RequiredArgsConstructor
public final class TasksController {
    private final TaskStatusRepository repository;
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatus getStatus(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TaskStatus> getStatuses() {
        return repository.findAll();
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatus createStatus(@RequestBody @Validated TaskStatusDto dto) {
        TaskStatus status = new TaskStatus();
        status.setName(dto.getName());
        status.setSlug(dto.getSlug());
        status.setCreatedAt(LocalDate.now());
        return repository.save(status);
    }
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatus updateStatus(@PathVariable Long id, @RequestBody @Validated TaskStatusDtoForUpdate dto) {
        var status = repository.findById(id).orElseThrow();
        if (dto.getName() != null) {
            status.setName(dto.getName());
        }
        if (dto.getSlug() != null) {
            status.setSlug(dto.getSlug());
        }
        return repository.save(status);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
