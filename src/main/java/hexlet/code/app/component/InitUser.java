package hexlet.code.app.component;

import hexlet.code.app.dto.UserDto;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.LabelRepository;
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
    private final LabelRepository labelRepository;

    public InitUser(UserService service, TaskStatusRepository repository, LabelRepository labelRepository) {
        this.service = service;
        this.repository = repository;
        this.labelRepository = labelRepository;
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
        for (String s : slugs) {
            TaskStatus status = new TaskStatus();
            status.setName(s + "Name");
            status.setSlug(s);
            status.setCreatedAt(LocalDate.now());
            repository.save(status);
        }
    }

    @PostConstruct
    public void createLabels() {
        var labels = List.of("feature", "bug");
        for (String l : labels) {
            Label label = new Label();
            label.setName(l);
            label.setCreatedAt(LocalDate.now());
            labelRepository.save(label);
        }
    }

}
