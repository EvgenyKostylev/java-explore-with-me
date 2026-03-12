package ru.practicum.explorewithme.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.HitDto;
import ru.practicum.explorewithme.StatDto;
import ru.practicum.explorewithme.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody @Valid HitDto request) {
        service.save(request);
    }

    @GetMapping("/stats")
    public List<StatDto> get(@RequestParam(name = "start", required = false)
                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime from,
                             @RequestParam(name = "end", required = false)
                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime to,
                             @RequestParam(name = "uris", required = false) List<String> uris,
                             @RequestParam(name = "unique", defaultValue = "false") boolean unique) {
        return service.get(from, to, uris, unique);
    }
}