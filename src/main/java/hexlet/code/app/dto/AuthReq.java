package hexlet.code.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthReq {
    private static final int MIN_PASSWORD_LENGTH = 3;
    @NotBlank
    private String username;
    @NotBlank
    @Size(min = MIN_PASSWORD_LENGTH)
    private String password;
}
