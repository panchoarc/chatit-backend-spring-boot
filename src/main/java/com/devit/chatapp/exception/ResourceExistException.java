package com.devit.chatapp.exception;

public class ResourceExistException extends RuntimeException {
    public ResourceExistException(String message) {
        super(message);
    }
}
