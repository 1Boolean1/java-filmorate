package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.director.DirectorService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
@Validated
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
    public DirectorDto addDirector(@RequestBody Director director) {
        log.info("Received POST /directors request with body: {}", director);
        return directorService.addDirector(director);
    }

    @PutMapping
    public DirectorDto updateDirector(@RequestBody Director director) {
        log.info("Received PUT /directors request with body: {}", director);
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{directorId}")
    public void deleteDirector(@PathVariable long directorId) {
        log.info("Received DELETE /directors/{} request", directorId);
        directorService.deleteDirector(directorId);

    }

}
