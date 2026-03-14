package ru.practicum.explorewithme.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class CompilationDto {
    private Set<EventShortDto> events;

    @NotNull
    private Integer id;

    @NotNull
    private Boolean pinned;

    @NotBlank
    private String title;
}