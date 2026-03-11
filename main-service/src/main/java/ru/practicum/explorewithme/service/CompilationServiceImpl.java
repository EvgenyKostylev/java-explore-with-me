package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.CompilationDto;
import ru.practicum.explorewithme.dto.NewCompilationDto;
import ru.practicum.explorewithme.dto.UpdateCompilationRequest;
import ru.practicum.explorewithme.expection.NotFoundException;
import ru.practicum.explorewithme.mapper.CompilationMapper;
import ru.practicum.explorewithme.model.Compilation;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.repository.CompilationRepository;
import ru.practicum.explorewithme.repository.EventRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper mapper;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> compilations = compilationRepository.findCompilations(pinned, pageable);

        log.info("Get compilations: {}", compilations.size());

        return mapper.toCompilationsDto(compilations);
    }

    @Override
    public CompilationDto getCompilation(int compId) {
        Optional<Compilation> compilation = compilationRepository.findById(compId);

        if (compilation.isPresent()) {
            log.info("Get compilation with id={}", compId);

            return mapper.toCompilationDto(compilation.get());
        } else {
            throw new NotFoundException(String.format("Compilation with id=%d was not found", compId));
        }
    }

    @Override
    @Transactional
    public CompilationDto saveCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = new Compilation();

        compilation.setPinned(newCompilationDto.getPinned() != null ? newCompilationDto.getPinned() : false);
        compilation.setTitle(newCompilationDto.getTitle());

        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());

            compilation.getEvents().addAll(events);
        }

        compilation = compilationRepository.save(compilation);

        log.info("Save compilation: {}", compilation);

        return mapper.toCompilationDto(compilation);
    }

    @Override
    public void deleteCompilation(int compId) {
        Compilation compilation = getCompilationById(compId);

        log.info("Delete compilation with id={}", compId);

        compilationRepository.delete(compilation);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(int compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = getCompilationById(compId);

        mapper.updateCompilation(updateCompilationRequest, compilation);

        if (updateCompilationRequest.getEvents() != null && !updateCompilationRequest.getEvents().isEmpty()) {
            List<Event> events = eventRepository.findAllById(updateCompilationRequest.getEvents());

            compilation.getEvents().clear();
            compilation.getEvents().addAll(events);
        }

        compilationRepository.save(compilation);

        log.info("Update compilation: {}", compilation);

        return mapper.toCompilationDto(compilation);
    }

    private Compilation getCompilationById(int compId) {
        Optional<Compilation> compilationOptional = compilationRepository.findById(compId);

        if (compilationOptional.isEmpty()) {
            throw new NotFoundException(String.format("Compilation with id=%d was not found", compId));
        } else {
            log.info("Find compilation with id={}", compId);

            return compilationOptional.get();
        }
    }
}