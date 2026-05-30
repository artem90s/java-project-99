package hexlet.code.app.service;

import hexlet.code.app.dto.UserDto;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
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
public class UserService implements UserDetailsService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    /**
     *
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
    public List<User> getUsers() {
        return repository.findAll();
    }

    /**
     * Создает нового юзера.
     *
     * @param dto данные для создания юзера
     * @return DTO созданного юзера
     */
    @Transactional
    public User save(UserDto dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setCreatedAt(LocalDate.now());
        return repository.save(user);
    }

    /**
     * Обновляет существующего юзера.
     *
     * @param id  идентификатор обновляемого юзера
     * @param dto новые данные юзера
     * @return DTO обновленного юзера
     */
    @Transactional
    public User updateUser(Long id, UserDto dto) {
        var user = repository.getById(id);
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setUpdatedAt(LocalDate.now());
        return repository.save(user);
    }

    /**
     * Возвращает юзера по ID.
     *
     * @param id идентификатор юзера
     * @return DTO юзера
     */
    public User getUser(Long id) {
        return repository.getById(id);
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
}
