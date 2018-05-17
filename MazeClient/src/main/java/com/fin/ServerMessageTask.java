package com.fin;

import com.fin.game.maze.Maze;
import com.fin.game.player.Position;
import javafx.concurrent.Task;

public class ServerMessageTask extends Task<ServerMessage> {
    private Connect server;

    public ServerMessageTask(Connect server) {
        this.server = server;
    }

    @Override
    protected ServerMessage call() {
        return new ServerMessage((Maze) server.waitResponse(),(Boolean) server.waitResponse(),  (Position) server.waitResponse(), (Position) server.waitResponse());
    }
}
