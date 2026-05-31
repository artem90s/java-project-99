package hexlet.code.mapper;

import hexlet.code.dto.UserResponse;
import hexlet.code.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toDto(User user);
}
