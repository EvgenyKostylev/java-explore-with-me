package ru.practicum.explorewithme;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatDto {
    @JsonProperty("app")
    private String appName;

    private String uri;

    @JsonProperty("hits")
    private Long hitCount;
}