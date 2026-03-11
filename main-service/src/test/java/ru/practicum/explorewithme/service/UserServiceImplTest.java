package ru.practicum.explorewithme.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.explorewithme.dto.NewUserRequest;
import ru.practicum.explorewithme.dto.UserDto;
import ru.practicum.explorewithme.expection.NotFoundException;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase
@Transactional
public class UserServiceImplTest {
    @Autowired
    private UserService service;

    @Autowired
    private UserRepository repository;

    @BeforeEach
    public void beforeEach() {
        repository.deleteAll();
    }

    @AfterAll
    public void afterAll() {
        repository.deleteAll();
    }

    @Test
    public void getUsers() {
        User user = new User();

        user.setName("name");
        user.setEmail("email");
        repository.save(user);

        List<UserDto> users = service.getUsers(null, 0, 10);

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(user.getName(), users.getFirst().getName());
    }

    @Test
    public void saveUser() {
        NewUserRequest newUserRequest = new NewUserRequest();

        newUserRequest.setName("name");
        newUserRequest.setEmail("email");

        UserDto userDto = service.saveUser(newUserRequest);

        assertNotNull(userDto);
        assertEquals(newUserRequest.getName(), userDto.getName());
        assertEquals(newUserRequest.getEmail(), userDto.getEmail());
    }

    @Test
    public void deleteUser() {
        NewUserRequest newUserRequest = new NewUserRequest();

        newUserRequest.setName("name");
        newUserRequest.setEmail("email");

        UserDto userDto = service.saveUser(newUserRequest);

        List<User> users = repository.findAll();

        assertNotNull(users);
        assertEquals(1, users.size());

        service.deleteUser(userDto.getId());
        users = repository.findAll();

        assertEquals(0, users.size());
    }

    @Test
    public void deleteNotExistingUser() {
        assertThrows(NotFoundException.class, () -> service.deleteUser(1000));
    }
}