package ru.practicum.explorewithme.expection;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}