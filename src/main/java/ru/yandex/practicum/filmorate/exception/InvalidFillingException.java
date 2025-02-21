package ru.yandex.practicum.filmorate.exception;

public class InvalidFillingException extends RuntimeException {
    public InvalidFillingException(String message) {
        super(message);
    }
}