package com.fin.maze.gameListeners;

import com.fin.Listener;
import com.fin.maze.gameEvent.TickEvent;

public interface TickHandler<T extends TickEvent> extends Listener {
    void handle(T t);
}
