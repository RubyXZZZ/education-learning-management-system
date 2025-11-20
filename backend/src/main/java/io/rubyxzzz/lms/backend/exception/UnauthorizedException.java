package io.rubyxzzz.lms.backend.exception;
/**
 * Unauthorized Exception
 * Thrown when user doesn't have permission for an operation
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
