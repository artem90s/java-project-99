package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TaskUpdateDto {
    private String title;
    private Integer index;
    private String content;
    private String status;
    @JsonProperty("assignee_id")
    private Long assigneeId;
    private List<Long> taskLabelIds;
}
