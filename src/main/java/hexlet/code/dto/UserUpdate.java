package hexlet.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdate {
    private static final int MIN_PASSWORD_LENGTH = 3;
    private String firstName;
    private String lastName;
    @Email(message = "Только формата email")
    private String email;
    @Size(min = MIN_PASSWORD_LENGTH, message = "Минимум 3 символа")
    private String password;

}
