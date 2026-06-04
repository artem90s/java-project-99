package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.dto.UserResponse;
import hexlet.code.dto.UserUpdate;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;

    /**
     * @param username the username identifying the user whose data is required.
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = repository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Юзер не найден " + username));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .build();
    }

    /**
     * Возвращает список пользователей.
     *
     * @return список список пользователей
     */
    public List<UserResponse> getUsers() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    /**
     * Создает нового юзера.
     *
     * @param dto данные для создания юзера
     * @return DTO созданного юзера
     */
    @Transactional
    public UserResponse save(UserDto dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setCreatedAt(LocalDate.now());
        return mapper.toDto(repository.save(user));
    }

    /**
     * Обновляет существующего юзера.
     *
     * @param id  идентификатор обновляемого юзера
     * @param dto новые данные юзера
     * @return DTO обновленного юзера
     */
    @Transactional
    public UserResponse updateUser(Long id, UserUpdate dto) {
        var user = repository.getById(id);
        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }

        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        user.setUpdatedAt(LocalDate.now());
        return mapper.toDto(repository.save(user));
    }

    /**
     * Возвращает юзера по ID.
     *
     * @param id идентификатор юзера
     * @return DTO юзера
     */
    public UserResponse getUser(Long id) {
        return mapper.toDto(repository.getById(id));
    }


    /**
     * Удаляет юзера по ID.
     *
     * @param id идентификатор удаляемого юзера
     */
    @Transactional
    public void deleteUser(Long id) {
        repository.deleteById(id);
    }

    /**
     * Проверка прав для метода удаления юзера.
     * @param email
     * @return
     */
    @Override
    public boolean hasAuth(String email) {
        return repository.findByEmail(email).isPresent();
    }
}
