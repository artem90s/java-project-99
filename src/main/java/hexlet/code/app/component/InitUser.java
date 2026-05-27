package hexlet.code.app.component;

import hexlet.code.app.dto.UserDto;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public final class InitUser {
    private final UserService service;
    private final TaskStatusRepository repository;

    public InitUser(UserService service, TaskStatusRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @PostConstruct
    public void createFirstUser() {
        var dto = new UserDto();
        dto.setEmail("hexlet@example.com");
        dto.setPassword("qwerty");
        service.save(dto);
    }
    @PostConstruct
    public void createStatuses() {
        var slugs = List.of("draft", "to_review", "to_be_fixed", "to_publish", "published");
        for (String s: slugs) {
            TaskStatus status = new TaskStatus();
            status.setName(s + "Name");
            status.setSlug(s);
            status.setCreatedAt(LocalDate.now());
            repository.save(status);
        }
    }

}
