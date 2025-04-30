package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.DirectorRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

@Repository
public class DirectorDBStorage {
    private final DirectorRepository directorRepository;

    public DirectorDBStorage(DirectorRepository directorRepository) {
        this.directorRepository = directorRepository;
    }

    public Director addDirector(Director director) {
        return directorRepository.save(director);
    }

    public Director updateDirector(Director director) {
        directorRepository.findById(director.getId())
                .orElseThrow(() -> new NotFoundException("Director with id " + director.getId() + " not found"));
        return directorRepository.update(director);
    }

    public Collection<Director> getDirectors() {
        return directorRepository.findAll();
    }

    public Director getDirectorById(long directorId) {
        return directorRepository.findById(directorId)
                .orElseThrow(() -> new NotFoundException("Director with id " + directorId + " not found"));
    }

    public void deleteDirector(long directorId) {
        directorRepository.findById(directorId)
                .orElseThrow(() -> new NotFoundException("Director with id " + directorId + " not found"));
        directorRepository.delete(directorId);
    }
}

