package hexlet.code.app.mapper;

import hexlet.code.app.dto.LabelResponse;
import hexlet.code.app.model.Label;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LabelMapper {
    LabelResponse toResponse(Label label);
}
