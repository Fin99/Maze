package com.fin.connects;

import com.fin.Listener;
import com.fin.connects.event.RestartGameEvent;

public interface RestartGameListener<T extends RestartGameEvent> extends Listener {
    void handle(T t);
}
