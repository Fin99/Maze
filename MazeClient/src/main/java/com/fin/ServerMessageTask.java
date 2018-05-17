package com.fin;

import com.fin.game.maze.Maze;
import javafx.concurrent.Task;

public class ServerMessageTask extends Task<ServerMessage> {
    private Connect server;

    public ServerMessageTask(Connect server) {
        this.server = server;
    }

    @Override
    protected ServerMessage call() {
        return new ServerMessage((Maze) server.waitResponse(), (Boolean) server.waitResponse());
    }
}
