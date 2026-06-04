package hexlet.code.controller;

import hexlet.code.dto.UserDto;
import hexlet.code.dto.UserResponse;
import hexlet.code.dto.UserUpdate;
import hexlet.code.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
public class UsersController {
    private final UserService service;

    public UsersController(UserService service) {
        this.service = service;
    }

    /**
     * Метод контроллера , который возвращает список юзеров.
     * @return список юзеров
     */
    @GetMapping()
    public ResponseEntity<List<UserResponse>> getUsers() {
        var users = service.getUsers();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(users.size()))
                .body(users);
    }

    /**
     * Метод добавления юзера.
     * @param dto
     * @return дто юзера
     */
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse addUser(@RequestBody @Valid UserDto dto) {
        return service.save(dto);
    }

    /**
     * Метод обновления юзера.
     * @param id
     * @param dto
     * @return дто юзера
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateUser(@PathVariable Long id, @RequestBody @Valid UserUpdate dto) {
        return service.updateUser(id, dto);
    }

    /**
     * Метод , котороый возвращет юзера по его айди.
     * @param id
     * @return дто юзера
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUser(@PathVariable Long id) {
        return service.getUser(id);
    }

    /**
     * Метод удаления юзера по айди.
     * в котором происходит проверка кастомным методом hasAdminRole()
     * @param id
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@userServiceImpl.hasAuth(authentication.name)")
    public void deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
    }
}
