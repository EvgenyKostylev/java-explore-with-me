package ru.practicum.explorewithme.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.explorewithme.model.State;

import java.time.LocalDateTime;

@Data
public class EventFullDto {
    @NotBlank
    private String annotation;

    @NotNull
    @Valid
    private CategoryDto category;

    private Integer confirmedRequests;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    private String description;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Integer id;

    @NotNull
    @Valid
    private UserShortDto initiator;

    @NotNull
    @Valid
    private Location location;

    @NotNull
    private Boolean paid;

    private Integer participantLimit;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    private Boolean requestModeration = true;

    private State state;

    @NotBlank
    private String title;

    private Integer views;
}