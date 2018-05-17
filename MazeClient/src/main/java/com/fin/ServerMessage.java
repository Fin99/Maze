package com.fin;

import com.fin.game.maze.Maze;
import com.fin.game.player.Position;

public class ServerMessage {
    private Maze maze;
    private boolean amIGoingNow;
    private Position bulletStart;
    private Position bulletFinish;

    public Position getBulletStart() {
        return bulletStart;
    }

    public Position getBulletFinish() {
        return bulletFinish;
    }

    public Maze getMaze() {
        return maze;
    }

    public boolean amIGoingNow() {
        return amIGoingNow;
    }

    public ServerMessage(Maze maze, boolean amIGoingNow, Position bulletStart, Position bulletFinish) {
        this.maze = maze;
        this.amIGoingNow = amIGoingNow;
        this.bulletStart = bulletStart;
        this.bulletFinish = bulletFinish;
    }
}
