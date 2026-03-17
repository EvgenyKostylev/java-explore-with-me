package ru.practicum.explorewithme.dto;

import lombok.Data;
import ru.practicum.explorewithme.model.Comment;

import java.util.List;
import java.util.Map;

@Data
public class EventStatsContext {
    private final Map<Integer, Integer> participants;

    private final Map<Integer, Integer> views;

    private final Map<Integer, List<Comment>> comments;
}