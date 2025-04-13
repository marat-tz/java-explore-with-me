package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> findCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> result = compilationRepository.findAllByPinned(pinned, pageable).getContent();
        return result.stream()
                .map(compilationMapper::toDto)
                .toList();
    }

    @Override
    public CompilationDto findCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Подборка " + compId + " не найдена"));

        return compilationMapper.toDto(compilation);
    }

    @Override
    public CompilationDto createCompilation(NewCompilationDto dto) {
        if (dto.getEvents() == null) {
            dto.setEvents(new ArrayList<>());
        }
        List<Long> eventIds = dto.getEvents();
        List<Event> events = eventRepository.findAllById(eventIds);
        Compilation compilation = compilationRepository.save(compilationMapper.toEntity(dto, events));
        return compilationMapper.toDto(compilation);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto dto) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Подборка " + compId + " не найдена"));

        if (dto.getEvents() == null) {
            dto.setEvents(new ArrayList<>());
        }

        compilation.setPinned(dto.getPinned());

        if (!dto.getEvents().isEmpty()) {
            List<Long> eventIds = dto.getEvents();
            List<Event> events = eventRepository.findAllById(eventIds);
            compilation.setEvents(events);
        }

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            compilation.setTitle(dto.getTitle());
        }

        return compilationMapper.toDto(compilationRepository.save(compilation));
    }

    @Override
    public void deleteCompilation(Long compId) {
        compilationRepository.deleteById(compId);
    }
}
