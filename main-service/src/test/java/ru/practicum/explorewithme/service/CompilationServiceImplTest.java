package ru.practicum.explorewithme.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.explorewithme.dto.CompilationDto;
import ru.practicum.explorewithme.dto.NewCompilationDto;
import ru.practicum.explorewithme.model.State;
import ru.practicum.explorewithme.dto.UpdateCompilationRequest;
import ru.practicum.explorewithme.model.*;
import ru.practicum.explorewithme.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase
@Transactional
public class CompilationServiceImplTest {
    @Autowired
    private CompilationService service;

    @Autowired
    private CompilationRepository compilationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private Event event;

    @BeforeEach
    public void beforeEach() {
        compilationRepository.deleteAll();
        eventRepository.deleteAll();
        locationRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setName("name");
        user.setEmail("email");
        user = userRepository.save(user);

        Category category = new Category();
        category.setName("name");
        category = categoryRepository.save(category);

        Location location = new Location();
        location.setLat(BigDecimal.valueOf(55.754167));
        location.setLon(BigDecimal.valueOf(37.62));
        location = locationRepository.save(location);

        event = new Event();
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
        event = eventRepository.save(event);
    }

    @AfterAll
    public void afterAll() {
        compilationRepository.deleteAll();
        eventRepository.deleteAll();
        locationRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void getCompilations() {
        Compilation compilation = new Compilation();

        compilation.setPinned(false);
        compilation.setTitle("title");
        compilation.setEvents(new HashSet<>(Set.of(event)));
        compilationRepository.save(compilation);

        List<CompilationDto> compilations = service.getCompilations(false, 0, 10);

        assertNotNull(compilations);
        assertEquals(1, compilations.size());
        assertEquals(compilation.getTitle(), compilations.getFirst().getTitle());
    }

    @Test
    public void getCompilation() {
        Compilation compilation = new Compilation();

        compilation.setPinned(false);
        compilation.setTitle("title");
        compilation.setEvents(new HashSet<>(Set.of(event)));
        compilation = compilationRepository.save(compilation);

        CompilationDto compilationDto = service.getCompilation(compilation.getId());

        assertNotNull(compilationDto);
        assertEquals(compilation.getTitle(), compilationDto.getTitle());
    }

    @Test
    public void saveCompilation() {
        NewCompilationDto newCompilationDto = new NewCompilationDto();

        newCompilationDto.setPinned(false);
        newCompilationDto.setTitle("title");
        newCompilationDto.setEvents(new HashSet<>(Set.of(event.getId())));

        CompilationDto compilationDto = service.saveCompilation(newCompilationDto);

        assertNotNull(compilationDto);
        assertEquals(compilationDto.getTitle(), newCompilationDto.getTitle());
    }

    @Test
    public void deleteCompilation() {
        Compilation compilation = new Compilation();

        compilation.setPinned(false);
        compilation.setTitle("title");
        compilation.setEvents(new HashSet<>(Set.of(event)));
        compilation = compilationRepository.save(compilation);
        service.deleteCompilation(compilation.getId());

        List<Compilation> compilations = compilationRepository.findAll();

        assertEquals(0, compilations.size());
    }

    @Test
    public void updateCompilation() {
        Compilation compilation = new Compilation();

        compilation.setPinned(false);
        compilation.setTitle("title");
        compilation.setEvents(new HashSet<>(Set.of(event)));
        compilation = compilationRepository.save(compilation);

        UpdateCompilationRequest updateCompilationRequest = new UpdateCompilationRequest();
        updateCompilationRequest.setPinned(false);
        updateCompilationRequest.setTitle("newTitle");
        updateCompilationRequest.setEvents(new HashSet<>(Set.of(event.getId())));

        CompilationDto compilationDto = service.updateCompilation(compilation.getId(), updateCompilationRequest);

        assertEquals(compilation.getId(), compilationDto.getId());
        assertEquals(updateCompilationRequest.getTitle(), compilationDto.getTitle());
    }
}