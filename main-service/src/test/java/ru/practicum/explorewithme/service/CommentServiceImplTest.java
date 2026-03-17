package ru.practicum.explorewithme.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.explorewithme.dto.CommentFullDto;
import ru.practicum.explorewithme.dto.NewCommentDto;
import ru.practicum.explorewithme.model.*;
import ru.practicum.explorewithme.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase
@Transactional
public class CommentServiceImplTest {
    @Autowired
    private CommentService service;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private User initiator;
    private User user;
    private Category category;
    private Location location;
    private Event event;

    @BeforeEach
    public void beforeEach() {
        participantRepository.deleteAll();
        eventRepository.deleteAll();
        locationRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        initiator = new User();
        initiator.setName("name");
        initiator.setEmail("initiatorEmail");
        initiator = userRepository.save(initiator);

        user = new User();
        user.setName("name");
        user.setEmail("userEmail");
        user = userRepository.save(user);

        category = new Category();
        category.setName("name");
        category = categoryRepository.save(category);

        location = new Location();
        location.setLat(BigDecimal.valueOf(55.754167));
        location.setLon(BigDecimal.valueOf(37.62));
        location = locationRepository.save(location);

        event = new Event();
        event.setAnnotation("annotation");
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now().minusHours(1));
        event.setDescription("description");
        event.setInitiator(initiator);
        event.setEventDate(LocalDateTime.now().minusHours(1));
        event.setLocation(location);
        event.setPaid(false);
        event.setParticipantLimit(0);
        event.setRequestModeration(false);
        event.setState(State.PUBLISHED);
        event.setTitle("title");
        event = eventRepository.save(event);

        Participant participant = new Participant();

        participant.setRequestor(user);
        participant.setCreated(LocalDateTime.now().minusHours(1));
        participant.setStatus(Status.CONFIRMED);
        participant.setEvent(event);
        participantRepository.save(participant);
    }

    @AfterAll
    public void afterAll() {
        commentRepository.deleteAll();
        participantRepository.deleteAll();
        eventRepository.deleteAll();
        locationRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void getComment() {
        Comment comment = new Comment();

        comment.setCommentator(user);
        comment.setCommentDate(LocalDateTime.now());
        comment.setComment("comment");
        comment.setCondition(Condition.PUBLISHED);
        comment.setEvent(event);
        comment = commentRepository.save(comment);

        CommentFullDto commentFullDto = service.getComment(comment.getId());

        assertNotNull(commentFullDto);
        assertEquals(comment.getId(), commentFullDto.getId());
        assertEquals(comment.getComment(), commentFullDto.getComment());
    }

    @Test
    public void getCommentsByAdmin() {
        Comment comment = new Comment();

        comment.setCommentator(user);
        comment.setCommentDate(LocalDateTime.now());
        comment.setComment("comment");
        comment.setCondition(Condition.PUBLISHED);
        comment.setEvent(event);
        comment = commentRepository.save(comment);

        List<CommentFullDto> comments = service.getComments(null, null, null, 0, 10);

        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals(comment.getId(), comments.getFirst().getId());
    }

    @Test
    public void updateCommentCondition() {
        Comment comment = new Comment();

        comment.setCommentator(user);
        comment.setCommentDate(LocalDateTime.now());
        comment.setComment("comment");
        comment.setCondition(Condition.PENDING);
        comment.setEvent(event);
        comment = commentRepository.save(comment);

        CommentFullDto commentFullDto = service.updateCommentCondition(comment.getId(), Condition.PUBLISHED);

        assertNotNull(commentFullDto);
        assertEquals(comment.getId(), commentFullDto.getId());
        assertEquals(comment.getComment(), commentFullDto.getComment());
        assertNotEquals(Condition.PENDING, commentFullDto.getCondition());
    }

    @Test
    public void getCommentsByCommentator() {
        Comment comment = new Comment();

        comment.setCommentator(user);
        comment.setCommentDate(LocalDateTime.now());
        comment.setComment("comment");
        comment.setCondition(Condition.PUBLISHED);
        comment.setEvent(event);
        comment = commentRepository.save(comment);

        List<CommentFullDto> comments = service.getComments(user.getId(), null, 0, 10);

        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals(comment.getId(), comments.getFirst().getId());
        assertEquals(comment.getCommentator().getId(), comments.getFirst().getCommentator().getId());
    }

    @Test
    public void saveComment() {
        NewCommentDto newCommentDto = new NewCommentDto();

        newCommentDto.setComment("comment");

        CommentFullDto comment = service.saveComment(user.getId(), event.getId(), newCommentDto);

        assertNotNull(comment);
        assertEquals(newCommentDto.getComment(), comment.getComment());
    }

    @Test
    public void updateComment() {
        Comment comment = new Comment();

        comment.setCommentator(user);
        comment.setCommentDate(LocalDateTime.now());
        comment.setComment("comment");
        comment.setCondition(Condition.PUBLISHED);
        comment.setEvent(event);
        comment = commentRepository.save(comment);

        NewCommentDto newCommentDto = new NewCommentDto();

        newCommentDto.setComment("newComment for test update comment");

        CommentFullDto commentFullDto = service.updateComment(user.getId(), comment.getId(), newCommentDto);

        assertNotNull(commentFullDto);
        assertEquals(comment.getId(), commentFullDto.getId());
        assertEquals(newCommentDto.getComment(), commentFullDto.getComment());
        assertEquals(Condition.EDITED, commentFullDto.getCondition());
    }
}