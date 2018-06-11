package com.fin.maze.gameEvent;

import com.fin.Event;

public class EndGameEvent implements Event {
    private final boolean winner;

    public EndGameEvent(boolean winner) {
        this.winner = winner;
    }

    public boolean isWinner() {
        return winner;
    }
}
