package ru.practicum.explorewithme.dto;

import lombok.Data;
import ru.practicum.explorewithme.model.Status;

import java.time.LocalDate;

@Data
public class ParticipationRequestDto {
    private LocalDate created;

    private Integer event;

    private Integer id;

    private Integer requester;

    private Status status;
}