package ru.practicum.explorewithme;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatDto {
    @JsonProperty("app")
    private String appName;

    private String uri;

    @JsonProperty("hits")
    private long hitCount;
}