package hexlet.code.mapper;

import hexlet.code.dto.LabelResponse;
import hexlet.code.model.Label;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LabelMapper {
    LabelResponse toResponse(Label label);
}
