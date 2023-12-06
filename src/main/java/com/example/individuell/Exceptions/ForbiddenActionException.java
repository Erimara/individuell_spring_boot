package com.example.individuell.Exceptions;

public class ForbiddenActionException extends Exception {
    public ForbiddenActionException(String message) {
        super(message);
    }
}