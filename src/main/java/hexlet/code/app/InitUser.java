package hexlet.code.app;

import hexlet.code.app.dto.UserDto;
import hexlet.code.app.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public final class InitUser {
    private final UserService service;

    public InitUser(UserService service) {
        this.service = service;
    }

    @PostConstruct
    public void createFirstUser() {
        var dto = new UserDto();
        dto.setEmail("hexlet@example.com");
        dto.setPassword("qwerty");
        service.save(dto);
    }

}
