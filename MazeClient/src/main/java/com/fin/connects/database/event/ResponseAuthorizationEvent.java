package com.fin.connects.database.event;

import com.fin.Event;

public class ResponseAuthorizationEvent implements Event {
    private final boolean result;

    public ResponseAuthorizationEvent(boolean result) {
        this.result = result;
    }

    public boolean isResult() {
        return result;
    }
}
