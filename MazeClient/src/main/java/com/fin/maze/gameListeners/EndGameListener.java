package com.fin.maze.gameListeners;

import com.fin.Listener;
import com.fin.maze.gameEvent.EndGameEvent;

public interface EndGameListener<T extends EndGameEvent> extends Listener {
    void handle(T t);
}
