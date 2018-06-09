package com.fin.turn;

import com.fin.Event;

public class TurnEvent implements Event {
    private final boolean move;

    public TurnEvent(boolean move) {
        this.move = move;

    }

    public boolean isMove() {
        return move;
    }
}
