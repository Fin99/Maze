package com.fin.maze.loginListener;

import com.fin.Listener;
import com.fin.connects.database.event.ResponseRegistrationEvent;

public interface ResponseRegistrationEventHandler<T extends ResponseRegistrationEvent> extends Listener {
    void handle(T t);
}
