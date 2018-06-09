package com.fin.connects;

import com.fin.Listener;
import com.fin.connects.event.ReplacementConnectEvent;

public interface ReplacementConnectListener<T extends ReplacementConnectEvent> extends Listener {
    void handle(ReplacementConnectEvent replacementConnectEvent);
}
