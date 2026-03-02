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
public class HitServiceImpl implements HitService {
    private final HitRepository repository;

    private static final LocalDateTime MIN_DATE_TIME =
            LocalDateTime.of(1970, 1, 1, 0, 0, 0);

    @Override
    public void save(HitDto hit) {
        repository.save(HitMapper.toHit(hit));

        log.info("save hit: {}", hit);
    }

    @Override
    public List<StatDto> get(LocalDateTime from, LocalDateTime to, List<String> uris, boolean unique) {
        List<StatDto> statsDto;

        if (from == null) {
            from = MIN_DATE_TIME;
        }

        if (to == null) {
            to = LocalDateTime.now();
        }

        if (unique) {
            statsDto = repository.findStatsByTimestampAndUrisUnique(from, to, uris);
        } else {
            statsDto = repository.findStatsByTimestampAndUris(from, to, uris);
        }

        log.info("get statsDto: {}", statsDto);

        return statsDto;
    }
}