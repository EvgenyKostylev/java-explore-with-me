package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.HitDto;
import ru.practicum.explorewithme.StatDto;
import ru.practicum.explorewithme.mapper.HitMapper;
import ru.practicum.explorewithme.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final HitRepository repository;

    @Override
    public void save(HitDto hit) {
        repository.save(HitMapper.toHit(hit));

        log.info("save hit: {}", hit);
    }

    @Override
    public List<StatDto> get(LocalDateTime from, LocalDateTime to, List<String> uris, boolean unique) {
        if (uris != null && uris.isEmpty()) {
            uris = null;
        }

        List<StatDto> statsDto = repository.findStats(from, to, uris, unique);

        log.info("get statsDto: {}", statsDto);

        return statsDto;
    }
}