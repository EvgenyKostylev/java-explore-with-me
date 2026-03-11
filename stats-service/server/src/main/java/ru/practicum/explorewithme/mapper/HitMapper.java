package ru.practicum.explorewithme.mapper;

import ru.practicum.explorewithme.HitDto;
import ru.practicum.explorewithme.model.Hit;

public class HitMapper {
    public static Hit toHit(HitDto hitDto) {
        Hit hit = new Hit();

        hit.setAppName(hitDto.getAppName());
        hit.setUri(hitDto.getUri());
        hit.setIp(hitDto.getIp());
        hit.setTimestamp(hitDto.getTimestamp());

        return hit;
    }
}