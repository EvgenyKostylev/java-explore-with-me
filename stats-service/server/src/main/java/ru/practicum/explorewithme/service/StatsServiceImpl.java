package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.HitDto;
import ru.practicum.explorewithme.StatDto;
import ru.practicum.explorewithme.mapper.HitMapper;
import ru.practicum.explorewithme.model.Hit;
import ru.practicum.explorewithme.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final HitRepository repository;
    private final HitMapper mapper;

    @Override
    public void save(HitDto hit) {
        Hit newHit = repository.save(mapper.toHit(hit));

        log.info("save hit: {}", newHit);
    }

    @Override
    public List<StatDto> get(LocalDateTime from, LocalDateTime to, List<String> uris, boolean unique) {
        if (uris != null && uris.isEmpty()) {
            uris = null;
        }

        List<StatDto> statsDto;

        if (unique) {
            statsDto = repository.findUniqueStats(from, to, uris);
        } else {
            statsDto = repository.findStats(from, to, uris);
        }

        log.info("get statsDto: {}", statsDto);

        return statsDto;
    }
}