package com.fin.maze.gameEvent;

import com.fin.Event;

public class TickEvent implements Event {
    private final int counter;
    private final boolean move;

    public TickEvent(int counter, boolean move) {
        this.counter = counter;
        this.move = move;
    }

    public int getCounter() {
        return counter;
    }

    public boolean isMove() {
        return move;
    }
}
