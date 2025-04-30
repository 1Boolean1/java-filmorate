package ru.yandex.practicum.filmorate.mappers;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class DirectorMapper {
    public static Director mapToDirector(Director request) {
        Director director = new Director();
        director.setId(request.getId());
        director.setName(request.getName());
        return director;
    }

    public static DirectorDto mapToDirectorDto(Director director) {
        DirectorDto directorDto = new DirectorDto();
        directorDto.setId(director.getId());
        directorDto.setName(director.getName());
        return directorDto;
    }

    public static Director toDirectorDTO(DirectorDto directorDto) {
        Director director = new Director();
        director.setId(directorDto.getId());
        director.setName(directorDto.getName());
        return director;
    }

    public static List<DirectorDto> mapToDirectorDtoList(List<Director> directors) {
        if (directors == null || directors.isEmpty()) {
            return Collections.emptyList();
        }
        return directors.stream()
                .map(DirectorMapper::mapToDirectorDto)
                .collect(Collectors.toList());
    }

}
