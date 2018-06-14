package com.fin.connects.server;

import com.fin.Listener;
import com.fin.connects.server.event.RestartGameEvent;

public interface RestartGameListener<T extends RestartGameEvent> extends Listener {
    void handle(T t);
}
