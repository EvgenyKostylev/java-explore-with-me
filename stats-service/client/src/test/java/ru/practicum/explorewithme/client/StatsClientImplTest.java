package ru.practicum.explorewithme.client;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.explorewithme.HitDto;
import ru.practicum.explorewithme.StatDto;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class StatsClientImplTest {
    @Test
    public void postRequestSaveHit() {
        RestTemplate rest = mock(RestTemplate.class);

        StatsClientImpl client =
                new StatsClientImpl(rest, "http://localhost:9090");
        HitDto hit = HitDto.builder()
                .appName("app")
                .uri("uri")
                .ip("ip")
                .timestamp(LocalDateTime.now())
                .build();

        client.save(hit);

        verify(rest).postForObject(
                "http://localhost:9090/hit",
                hit,
                Void.class
        );
    }

    @Test
    public void getRequestGetStats() {
        LocalDateTime now = LocalDateTime.now();
        RestTemplate rest = mock(RestTemplate.class);

        StatsClientImpl client =
                new StatsClientImpl(rest, "http://localhost:9090");
        List<StatDto> stats = List.of(
                StatDto.builder()
                        .appName("name")
                        .uri("uri")
                        .hitCount(1L)
                        .build());
        ResponseEntity<List<StatDto>> response =
                ResponseEntity.ok(stats);

        when(rest.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<StatDto>>>any()
        )).thenReturn(response);

        List<StatDto> result = client.get(
                now.minusHours(1), now.plusHours(1),
                List.of("/uri"),
                false
        );

        assertThat(result).isEqualTo(stats);
    }
}