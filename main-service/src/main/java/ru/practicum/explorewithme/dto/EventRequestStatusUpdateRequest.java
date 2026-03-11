package ru.practicum.explorewithme.dto;

import lombok.Data;
import ru.practicum.explorewithme.model.Status;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
    private List<Integer> requestIds;

    private Status status;
}