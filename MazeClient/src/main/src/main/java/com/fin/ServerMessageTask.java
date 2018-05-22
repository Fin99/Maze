package com.fin;

import com.fin.game.maze.Maze;
import com.fin.game.player.Position;
import javafx.concurrent.Task;

public class ServerMessageTask extends Task<ServerMessage> {

    @Override
    protected ServerMessage call() {
        Maze maze = (Maze) Connect.waitResponse();
        Boolean aBoolean = (Boolean) Connect.waitResponse();
        if (aBoolean == null) {
            failed();
        }
        Position position = (Position) Connect.waitResponse();
        Position position1 = (Position) Connect.waitResponse();
        return new ServerMessage(maze, aBoolean, position, position1);
        ///return new ServerMessage((Maze) Connect.waitResponse(),(Boolean) Connect.waitResponse(),  (Position) Connect.waitResponse(), (Position) Connect.waitResponse());
    }
}
