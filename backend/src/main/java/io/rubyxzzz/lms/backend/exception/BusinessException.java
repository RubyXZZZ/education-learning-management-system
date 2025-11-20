package io.rubyxzzz.lms.backend.exception;

/**
 * Exception for business logic violations
 * Results in HTTP 409 (Conflict)
 */
class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
