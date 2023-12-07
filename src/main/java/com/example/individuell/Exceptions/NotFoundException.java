package com.example.individuell.Exceptions;

/**
 * Custom exception class which takes in a string in a constructor enabling for custom error handling
 */
public class NotFoundException extends Exception{
    public NotFoundException(String message) {
        super(message);
    }
}
