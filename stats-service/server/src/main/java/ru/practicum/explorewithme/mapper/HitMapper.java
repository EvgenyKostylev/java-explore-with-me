package ru.practicum.explorewithme.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explorewithme.HitDto;
import ru.practicum.explorewithme.model.Hit;

@Mapper(componentModel = "spring")
public interface HitMapper {
    @Mapping(target = "id", ignore = true)
    Hit toHit(HitDto hitDto);
}