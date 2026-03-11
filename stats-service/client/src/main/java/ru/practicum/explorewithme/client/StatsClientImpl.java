package ru.practicum.explorewithme.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.explorewithme.HitDto;
import ru.practicum.explorewithme.StatDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StatsClientImpl implements StatsClient {
    private final RestTemplate rest;
    private final String statsServerUrl;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClientImpl(RestTemplate rest, String statsServerUrl) {
        this.rest = rest;
        this.statsServerUrl = statsServerUrl;
    }

    @Override
    public void save(HitDto hit) {
        rest.postForObject(statsServerUrl + "/hit", hit, Void.class);
    }

    @Override
    public List<StatDto> get(LocalDateTime from, LocalDateTime to, List<String> uris, Boolean unique) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(statsServerUrl + "/stats");

        if (from != null) {
            builder.queryParam("from", from.format(formatter));
        }

        if (to != null) {
            builder.queryParam("to", to.format(formatter));
        }

        if (uris != null && !uris.isEmpty()) {
            builder.queryParam("uris", uris);
        }

        if (unique != null) {
            builder.queryParam("unique", unique);
        }

        ResponseEntity<List<StatDto>> response = rest.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }
}