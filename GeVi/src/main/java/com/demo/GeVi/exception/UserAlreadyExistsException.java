package com.demo.GeVi.exception;

public class UserAlreadyExistsException extends RuntimeException {
        public UserAlreadyExistsException(String message) {
        super(message);
    }
}
