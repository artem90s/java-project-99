package hexlet.code.app.mapper;

import hexlet.code.app.dto.UserResponse;
import hexlet.code.app.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toDto(User user);
}
