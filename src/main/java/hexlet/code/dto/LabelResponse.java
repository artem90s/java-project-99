package hexlet.code.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class LabelResponse {
    private Long id;
    private String name;
    private LocalDate createdAt;
}
