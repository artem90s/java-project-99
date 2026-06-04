package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.dto.UserResponse;
import hexlet.code.dto.UserUpdate;

import java.util.List;

public interface UserService {
    List<UserResponse> getUsers();

    UserResponse save(UserDto dto);

    UserResponse updateUser(Long id, UserUpdate dto);

    UserResponse getUser(Long id);

    void deleteUser(Long id);
    boolean hasAuth(String email);
}
