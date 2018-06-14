package com.fin.maze.loginListener;

import com.fin.Listener;
import com.fin.connects.database.event.ResponseAuthorizationEvent;

public interface ResponseAuthorizationEventHandler<T extends ResponseAuthorizationEvent> extends Listener {
    void handle(T t);
}
