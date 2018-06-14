package com.fin.connects.server.event;

import com.fin.Event;
import com.fin.ServerMessage;

public class ServerEvent implements Event {
    private final ServerMessage message;

    public ServerEvent(ServerMessage message) {
        this.message = message;
    }

    public ServerMessage getMessage() {
        return message;
    }
}
