package com.locusintellect;

public class FailToSendEmailException extends RuntimeException {

    public FailToSendEmailException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
