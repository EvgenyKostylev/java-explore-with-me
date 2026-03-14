package ru.practicum.explorewithme.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.explorewithme.dto.CategoryDto;
import ru.practicum.explorewithme.dto.NewCategoryDto;
import ru.practicum.explorewithme.model.State;
import ru.practicum.explorewithme.expection.ConflictException;
import ru.practicum.explorewithme.expection.NotFoundException;
import ru.practicum.explorewithme.model.Category;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.model.Location;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.repository.CategoryRepository;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.LocationRepository;
import ru.practicum.explorewithme.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase
@Transactional
public class CategoryServiceImplTest {
    @Autowired
    private CategoryService service;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void beforeEach() {
        eventRepository.deleteAll();
        locationRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterAll
    public void afterAll() {
        eventRepository.deleteAll();
        locationRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void saveCategory() {
        NewCategoryDto newCategoryDto = new NewCategoryDto();

        newCategoryDto.setName("name");

        CategoryDto categoryDto = service.saveCategory(newCategoryDto);

        assertNotNull(categoryDto);
        assertEquals(newCategoryDto.getName(), categoryDto.getName());
    }

    @Test
    public void deleteCategory() {
        NewCategoryDto newCategoryDto = new NewCategoryDto();

        newCategoryDto.setName("name");

        CategoryDto categoryDto = service.saveCategory(newCategoryDto);

        List<Category> categories = categoryRepository.findAll();

        assertNotNull(categories);
        assertEquals(1, categories.size());

        service.deleteCategory(categoryDto.getId());

        categories = categoryRepository.findAll();
        assertEquals(0, categories.size());
    }

    @Test
    public void deleteCategoryWithExistEvent() {
        User user = new User();
        user.setName("name");
        user.setEmail("email");
        user = userRepository.save(user);

        Category category = new Category();
        category.setName("name");
        category = categoryRepository.save(category);

        int categoryId = category.getId();
        Location location = new Location();
        location.setLat(BigDecimal.valueOf(55.754167));
        location.setLon(BigDecimal.valueOf(37.62));
        locationRepository.save(location);

        Event event = new Event();
        event.setAnnotation("annotation");
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription("description");
        event.setInitiator(user);
        event.setEventDate(LocalDateTime.now().plusHours(1));
        event.setLocation(location);
        event.setPaid(false);
        event.setParticipantLimit(0);
        event.setRequestModeration(false);
        event.setState(State.PUBLISHED);
        event.setTitle("title");
        eventRepository.save(event);

        assertThrows(ConflictException.class, () -> service.deleteCategory(categoryId));
    }

    @Test
    public void updateCategory() {
        NewCategoryDto newCategoryDto = new NewCategoryDto();

        newCategoryDto.setName("name");

        CategoryDto categoryDto = service.saveCategory(newCategoryDto);
        CategoryDto updateCategoryDto = new CategoryDto();

        updateCategoryDto.setName("newName");

        CategoryDto updatedCategoryDto = service.updateCategory(categoryDto.getId(), updateCategoryDto);

        assertEquals(categoryDto.getId(), updatedCategoryDto.getId());
        assertEquals(updateCategoryDto.getName(), updatedCategoryDto.getName());
    }

    @Test
    public void getCategories() {
        Category firstCategory = new Category();

        firstCategory.setName("firstName");
        categoryRepository.save(firstCategory);

        Category secondCategory = new Category();

        secondCategory.setName("secondName");
        categoryRepository.save(secondCategory);

        List<CategoryDto> categories = service.getCategories(0, 10);

        assertNotNull(categories);
        assertEquals(2, categories.size());
    }

    @Test
    public void getCategory() {
        NewCategoryDto newCategoryDto = new NewCategoryDto();

        newCategoryDto.setName("name");

        CategoryDto categoryDto = service.saveCategory(newCategoryDto);
        CategoryDto findedCategoryDto = service.getCategory(categoryDto.getId());

        assertEquals(categoryDto.getName(), findedCategoryDto.getName());
    }

    @Test
    public void getNotExistingCategory() {
        assertThrows(NotFoundException.class, () -> service.getCategory(1000));
    }
}