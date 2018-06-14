package com.fin.connects.database.event;

public class ResponseRegistrationEvent {
    private final boolean result;

    public ResponseRegistrationEvent(boolean result) {
        this.result = result;
    }

    public boolean isResult() {
        return result;
    }
}
