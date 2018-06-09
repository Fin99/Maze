package com.fin.maze.gameListeners;

import com.fin.Listener;
import com.fin.maze.gameEvent.MazeEvent;

public interface MazeListener<T extends MazeEvent> extends Listener {
    void handle(T t);
}
