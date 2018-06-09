package com.fin.maze.gameEvent;

import com.fin.Event;

public class ResizeEvent implements Event {
    private final int size;

    public ResizeEvent(int size) {

        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
