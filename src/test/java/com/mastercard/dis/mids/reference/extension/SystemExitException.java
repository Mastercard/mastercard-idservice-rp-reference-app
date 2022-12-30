package com.mastercard.dis.mids.reference.extension;

public final class SystemExitException extends SecurityException {
    private int statusCode;

    public SystemExitException(int status) {
        super("System Exit call has been intercepted by SystemExitExtension");
        statusCode = status;
    }

    public int getStatusCode() {
        return statusCode;
    }
}