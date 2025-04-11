package ru.practicum.compilation.service;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> findCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto findCompilationById(Long compId);

    CompilationDto createCompilation(NewCompilationDto dto);

    CompilationDto updateCompilation(Long compId, NewCompilationDto dto);

    void deleteCompilation(Long compId);

}
