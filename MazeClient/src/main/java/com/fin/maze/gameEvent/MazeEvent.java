package com.fin.maze.gameEvent;

import com.fin.Event;
import com.fin.game.cover.Cover;

public class MazeEvent implements Event {
    private final Cover cover;

    public MazeEvent(Cover cover) {
        this.cover = cover;
    }

    public Cover getCover() {
        return cover;
    }
}
