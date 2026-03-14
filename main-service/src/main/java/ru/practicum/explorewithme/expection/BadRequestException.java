package ru.practicum.explorewithme.expection;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}