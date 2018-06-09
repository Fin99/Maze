package com.fin.maze.gameListeners;

import com.fin.Listener;
import com.fin.maze.gameEvent.PlayersEvent;

public interface PlayersListener<T extends PlayersEvent>extends Listener {
    void handle(T t);
}
