package com.example.individuell.Exceptions;

/**
 * Custom exception class which takes in a string in a constructor enabling for custom error handling
 */
public class NonUniqueEmailException extends Exception {
    public NonUniqueEmailException(String message) {
        super(message);
    }
}