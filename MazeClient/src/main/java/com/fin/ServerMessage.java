package com.fin;

import com.fin.game.maze.Maze;

public class ServerMessage {
    private Maze maze;
    private boolean amIGoingNow;

    public Maze getMaze() {
        return maze;
    }

    public boolean amIGoingNow() {
        return amIGoingNow;
    }

    public ServerMessage(Maze maze, boolean amIGoingNow) {

        this.maze = maze;
        this.amIGoingNow = amIGoingNow;
    }
}
