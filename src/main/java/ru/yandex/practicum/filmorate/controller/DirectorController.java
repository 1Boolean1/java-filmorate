package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.service.director.DirectorService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public Collection<DirectorDto> getDirectors() {
        log.info("Received GET /directors request");
        return directorService.getDirectors();
    }

    @GetMapping("/{directorId}")
    public DirectorDto getDirectorById(@PathVariable long directorId) {
        log.info("Received GET /directors/{} request", directorId);
        return directorService.getDirectorById(directorId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DirectorDto addDirector(@RequestBody @Valid DirectorDto director) {
        log.info("Received POST /directors request with body: {}", director);
        return directorService.addDirector(director);
    }

    @PutMapping
    public DirectorDto updateDirector(@RequestBody @Valid DirectorDto director) {
        log.info("Received PUT /directors request with body: {}", director);
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{directorId}")
    public void deleteDirector(@PathVariable long directorId) {
        log.info("Received DELETE /directors/{} request", directorId);
        directorService.deleteDirector(directorId);

    }

}
