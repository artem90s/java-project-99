package hexlet.code.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LabelDto {
    public static final int MIN = 3;
    public static final int MAX = 1000;
    @NotEmpty
    @Size(min = MIN, max = MAX)
    private String name;
}
