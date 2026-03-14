package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.dto.NewUserRequest;
import ru.practicum.explorewithme.dto.UserDto;
import ru.practicum.explorewithme.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Integer> ids, int from, int size);

    UserDto saveUser(NewUserRequest newUserRequest);

    void deleteUser(int userId);

    User getUserById(int userId);
}