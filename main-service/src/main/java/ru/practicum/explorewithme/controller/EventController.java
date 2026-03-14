package ru.practicum.explorewithme.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.HitDto;
import ru.practicum.explorewithme.client.StatsClient;
import ru.practicum.explorewithme.dto.EventFullDto;
import ru.practicum.explorewithme.dto.EventShortDto;
import ru.practicum.explorewithme.dto.Sort;
import ru.practicum.explorewithme.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class EventController {
    private final EventService service;
    private final StatsClient statsClient;

    @Value("${spring.application.name}")
    private String appName;

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(
                                                 name = "text",
                                                 required = false) String text,
                                         @RequestParam(
                                                 name = "categories",
                                                 required = false) List<Integer> categories,
                                         @RequestParam(
                                                 name = "paid",
                                                 required = false) Boolean paid,
                                         @RequestParam(
                                                 name = "rangeStart",
                                                 required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                         @RequestParam(
                                                 name = "rangeEnd",
                                                 required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                         @RequestParam(
                                                 name = "onlyAvailable",
                                                 defaultValue = "false") boolean onlyAvailable,
                                         @RequestParam(
                                                 name = "sort",
                                                 required = false) Sort sort,
                                         @RequestParam(
                                                 name = "from",
                                                 defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(
                                                 name = "size",
                                                 defaultValue = "10") @Positive int size, HttpServletRequest request) {
        statsClient.save(HitDto.builder()
                .appName(appName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now()).build());

        return service.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable int id, HttpServletRequest request) {
        statsClient.save(HitDto.builder()
                .appName(appName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now()).build());

        return service.getEvent(id);
    }
}