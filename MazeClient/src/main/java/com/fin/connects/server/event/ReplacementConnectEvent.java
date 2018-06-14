package com.fin.connects.server.event;

import com.fin.Event;
import com.fin.connects.server.Connect;

public class ReplacementConnectEvent implements Event {
    private final Connect connect;

    public ReplacementConnectEvent(Connect connect) {
        this.connect = connect;
    }

    public Connect getConnect() {
        return connect;
    }
}
