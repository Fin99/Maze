package com.fin.maze.localHandlers;

import com.fin.Listener;
import com.fin.maze.localEvent.LocalEvent;

public interface LocalHandler<T extends LocalEvent> extends Listener {
    void handle(T t);
}
