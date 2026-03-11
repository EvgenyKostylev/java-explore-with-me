package ru.practicum.explorewithme.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.explorewithme.dto.NewUserRequest;
import ru.practicum.explorewithme.dto.UserDto;
import ru.practicum.explorewithme.dto.UserShortDto;
import ru.practicum.explorewithme.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Named("toFull")
    UserDto toUserDto(User user);

    List<UserDto> toUsersDto(List<User> users);

    @Named("toShort")
    UserShortDto toUserShortDto(User user);

    @Mapping(target = "id", ignore = true)
    User toUser(NewUserRequest newUserRequest);
}