package io.rubyxzzz.lms.backend.exception;

/**
 * Authentication Exception
 * Thrown when authentication fails
 *
 * Examples:
 * - User not found
 * - Account not active
 * - Email not verified
 */
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
