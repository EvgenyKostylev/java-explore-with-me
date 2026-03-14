package ru.practicum.explorewithme.mapper;

import org.mapstruct.*;
import ru.practicum.explorewithme.dto.CompilationDto;
import ru.practicum.explorewithme.dto.UpdateCompilationRequest;
import ru.practicum.explorewithme.model.Compilation;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {EventMapper.class})
public interface CompilationMapper {
    @Mapping(
            source = "events",
            target = "events",
            qualifiedByName = "toShortWithoutStats"
    )
    CompilationDto toCompilationDto(Compilation compilation);

    @Mapping(
            source = "events",
            target = "events",
            qualifiedByName = "toShortWithoutStats"
    )
    List<CompilationDto> toCompilationsDto(List<Compilation> compilations);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    void updateCompilation(UpdateCompilationRequest updateCompilationRequest, @MappingTarget Compilation compilation);
}