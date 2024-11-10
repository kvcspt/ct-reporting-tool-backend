package hu.kvcspt.ctreportingtoolbackend.mapper;

import hu.kvcspt.ctreportingtoolbackend.dto.UserDTO;
import hu.kvcspt.ctreportingtoolbackend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toEntity(UserDTO userDTO);

    UserDTO fromEntity(User user);
}
