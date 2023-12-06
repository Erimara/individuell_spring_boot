package com.example.individuell.Exceptions;

public class NonUniqueEmailException extends Exception {
    public NonUniqueEmailException(String message) {
        super(message);
    }
}