package ru.practicum.explorewithme.client;

import ru.practicum.explorewithme.HitDto;
import ru.practicum.explorewithme.StatDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsClient {
    void save(HitDto hit);

    List<StatDto> get(LocalDateTime from, LocalDateTime to, List<String> uris, Boolean unique);
}