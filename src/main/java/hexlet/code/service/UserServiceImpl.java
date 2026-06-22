package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.dto.UserResponse;
import hexlet.code.dto.UserUpdate;
import hexlet.code.exception.ResourceInUseException;
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
                .orElseThrow(() -> new UsernameNotFoundException("Юзер не найден " + username));
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
        User user = mapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
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
        var user = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Юзер не найден " + id));
        mapper.updateEntity(dto, user);

        if (dto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        return mapper.toDto(repository.save(user));
    }

    /**
     * Возвращает юзера по ID.
     *
     * @param id идентификатор юзера
     * @return DTO юзера
     */
    public UserResponse getUser(Long id) {
        var user = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Юзер не найден " + id));
        return mapper.toDto(user);
    }


    /**
     * Удаляет юзера по ID.
     *
     * @param id идентификатор удаляемого юзера
     */
    @Transactional
    public void deleteUser(Long id) {
        User user = repository.findById(id).orElseThrow();
        if (!user.getTasks().isEmpty()) {
            throw new ResourceInUseException("У пользователя есть задачи");
        }
        repository.deleteById(id);
    }

    /**
     * Проверка прав для метода удаления юзера.
     *
     * @param email
     * @return
     */
    @Override
    public boolean hasAuth(String email) {
        return repository.existsByEmail(email);
    }
}
