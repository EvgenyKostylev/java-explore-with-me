package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.HitDto;
import ru.practicum.explorewithme.StatDto;
import ru.practicum.explorewithme.service.HitService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class HitController {
    private final HitService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody HitDto request) {
        service.save(request);
    }

    @GetMapping("/stats")
    public List<StatDto> get(@RequestParam(name = "from", required = false)
                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime from,
                             @RequestParam(name = "to", required = false)
                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime to,
                             @RequestParam(name = "uris") List<String> uris,
                             @RequestParam(name = "unique", defaultValue = "false") boolean unique) {
        return service.get(from, to, uris, unique);
    }
}