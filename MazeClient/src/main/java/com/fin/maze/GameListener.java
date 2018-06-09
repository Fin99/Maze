package com.fin.maze;

import com.fin.Listener;
import com.fin.connects.event.ServerEvent;

public interface GameListener<T extends ServerEvent> extends Listener {
    void handle(T t);
}
