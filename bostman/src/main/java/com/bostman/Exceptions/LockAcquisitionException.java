package com.bostman.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.LOCKED)
public class LockAcquisitionException extends RuntimeException {
    public LockAcquisitionException(String message) {
        super(message);
    }

    public LockAcquisitionException(String message, Throwable cause) {
        super(message, cause);
    }
}