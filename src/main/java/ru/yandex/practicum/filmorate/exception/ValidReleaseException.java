package ru.yandex.practicum.filmorate.exception;

public class ValidReleaseException extends RuntimeException {
    public ValidReleaseException(String message) {
        super(message);
    }
}