package com.udea.fe.exception;

public class AlreadyInTeamException extends RuntimeException {
    public AlreadyInTeamException(String message) {
        super(message);
    }
}