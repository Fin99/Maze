package com.fin.maze.gameListeners;

import com.fin.Listener;
import com.fin.maze.gameEvent.ResizeEvent;

public interface ResizeListener<T extends ResizeEvent> extends Listener {
    void handle(T t);
}
