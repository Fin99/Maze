package com.fin.connects.server;

import com.fin.Listener;
import com.fin.connects.server.event.ReplacementConnectEvent;

public interface ReplacementConnectListener<T extends ReplacementConnectEvent> extends Listener {
    void handle(ReplacementConnectEvent replacementConnectEvent);
}
