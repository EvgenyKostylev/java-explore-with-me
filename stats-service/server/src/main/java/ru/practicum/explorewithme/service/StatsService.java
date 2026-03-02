package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.HitDto;
import ru.practicum.explorewithme.StatDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void save(HitDto hit);

    List<StatDto> get(LocalDateTime from, LocalDateTime to, List<String> uris, boolean unique);
}