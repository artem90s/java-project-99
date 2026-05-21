package hexlet.code.app.controller;

import hexlet.code.app.dto.UserDto;
import hexlet.code.app.dto.UserResponse;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.service.UserService;
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

import java.util.List;

@RestController
@RequestMapping("/api/users")
public final class UsersController {
    private final UserService service;
    private final UserMapper mapper;

    public UsersController(UserService service, UserMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getUsers() {
        return service.getUsers().stream().map(mapper::toDto).toList();
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse addUser(@RequestBody @Validated UserDto dto) {
        return mapper.toDto(service.save(dto));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateUser(@PathVariable Long id, @RequestBody @Validated UserDto dto) {
        return mapper.toDto(service.updateUser(id, dto));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUser(@PathVariable Long id) {
        return mapper.toDto(service.getUser(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
    }
}
