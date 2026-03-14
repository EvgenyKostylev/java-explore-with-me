package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.dto.NewUserRequest;
import ru.practicum.explorewithme.dto.UserDto;
import ru.practicum.explorewithme.expection.NotFoundException;
import ru.practicum.explorewithme.mapper.UserMapper;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public List<UserDto> getUsers(List<Integer> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<User> users = repository.findUsers(ids, pageable);

        log.info("Get users: {}", users.size());

        return mapper.toUsersDto(users);
    }

    @Override
    public UserDto saveUser(NewUserRequest newUserRequest) {
        User user = repository.save(mapper.toUser(newUserRequest));

        log.info("Save user: {}", user);

        return mapper.toUserDto(user);
    }

    @Override
    public void deleteUser(int userId) {
        User user = getUserById(userId);

        log.info("Delete user with id={}", userId);

        repository.delete(user);
    }

    public User getUserById(int userId) {
        Optional<User> userOptional = repository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        } else {
            log.info("Find user with id={}", userId);

            return userOptional.get();
        }
    }
}