package hexlet.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private static final int MIN_PASSWORD_LENGTH = 3;
    private Long id;
    private String firstName;
    private String lastName;
    @NotNull
    @Email(message = "Только формата email")
    private String email;
    @NotNull
    @Size(min = MIN_PASSWORD_LENGTH, message = "Минимум 3 символа")
    private String password;
}
