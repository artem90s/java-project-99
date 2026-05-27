package hexlet.code.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthReq {
    private static final int MIN_PASSWORD_LENGTH = 3;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
