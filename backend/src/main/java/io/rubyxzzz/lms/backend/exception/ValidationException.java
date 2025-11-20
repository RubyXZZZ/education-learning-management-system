package io.rubyxzzz.lms.backend.exception;

/**
 * Exception for validation errors
 * Results in HTTP 400
 */
class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
