package com.fin;

import com.fin.game.maze.Maze;
import javafx.concurrent.Task;

public class ServerMessageTask extends Task<ServerMessage> {
    private Connect server;

    public ServerMessageTask(Connect server) {
        this.server = server;
    }

    @Override
    protected ServerMessage call() throws Exception {
        Maze maze = (Maze) server.waitResponse();
        Boolean amIGoingNow = (Boolean) server.waitResponse();
        return new ServerMessage(maze, amIGoingNow);
    }
}
