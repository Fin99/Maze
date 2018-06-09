package com.fin.maze.gameListeners;

import com.fin.Listener;
import com.fin.maze.gameEvent.MoveEvent;

public interface MoveListener<T extends MoveEvent> extends Listener {
    void handle(T t);
}
