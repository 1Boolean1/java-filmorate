package ru.yandex.practicum.filmorate.service.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.mappers.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorDBStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorDBStorage directorStorage;

    public Collection<DirectorDto> getDirectors() {
        log.info("Getting all directors");
        Collection<Director> directors = directorStorage.getDirectors();
        return directors.stream()
                .map(DirectorMapper::mapToDirectorDto)
                .collect(Collectors.toList());
    }

    public DirectorDto getDirectorById(long directorId) {
        log.info("Getting director by id: {}", directorId);
        Director director = directorStorage.getDirectorById(directorId);
        return DirectorMapper.mapToDirectorDto(director);
    }

    public DirectorDto addDirector(DirectorDto directorDto) {
        log.info("Adding new director: {}", directorDto.getName());
        Director director = DirectorMapper.toDirectorDTO(directorDto);
        Director newDirector = directorStorage.addDirector(director);
        log.info("Director {} added with id: {}", newDirector.getName(), newDirector.getId());
        return DirectorMapper.mapToDirectorDto(newDirector);
    }

    public DirectorDto updateDirector(DirectorDto directorDto) {
        log.info("Updating director with id: {}", directorDto.getId());
        Director director = DirectorMapper.toDirectorDTO(directorDto);
        Director updateDirector = directorStorage.updateDirector(director);
        log.info("Director {} with id: {} updated", updateDirector.getName(), updateDirector.getId());
        return DirectorMapper.mapToDirectorDto(updateDirector);
    }

    public void deleteDirector(long directorId) {
        log.info("Deleting director by id: {}", directorId);
        directorStorage.deleteDirector(directorId);
    }

}
