package ru.practicum.explorewithme.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.practicum.explorewithme.client.StatsClient;
import ru.practicum.explorewithme.client.StatsClientImpl;

@Configuration
public class StatsClientConfig {
    @Value("${stats-server.url}")
    private String serverUrl;

    @Bean
    public StatsClient statsClient() {
        return new StatsClientImpl(new RestTemplate(), serverUrl);
    }
}