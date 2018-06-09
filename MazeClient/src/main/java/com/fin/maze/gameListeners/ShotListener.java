package com.fin.maze.gameListeners;

import com.fin.Listener;
import com.fin.maze.gameEvent.ShotEvent;

public interface ShotListener<T extends ShotEvent> extends Listener {
    void handle(T t);
}
