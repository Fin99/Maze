package com.fin;

import com.fin.game.cover.Direction;
import com.fin.game.maze.Maze;
import com.fin.game.player.Player;
import com.fin.game.player.Position;

import java.io.Serializable;

public class ServerMessage implements Serializable {
    private final String type;

    private final Maze maze;
    private final Boolean move;
    private final Player player;
    private final Direction direction;
    private final Position startPosition;
    private final Position finishPosition;
    private final Position keyInMazePosition;
    private final Position gunInMazePosition;

    public ServerMessage(String type, Maze maze, Boolean move, Player player, Direction direction, Position startPosition, Position finishPosition, Position keyInMazePosition, Position gunInMazePosition) {
        this.type = type;
        this.maze = maze;
        this.move = move;
        this.player = player;
        this.direction = direction;

        this.startPosition = startPosition;
        this.finishPosition = finishPosition;
        this.keyInMazePosition = keyInMazePosition;
        this.gunInMazePosition = gunInMazePosition;
    }

    public String getType() {
        return type;
    }

    public Direction getDirection() {
        return direction;
    }

    public Maze getMaze() {
        return maze;
    }

    public Boolean getMove() {
        return move;
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public Position getFinishPosition() {
        return finishPosition;
    }

    public Player getPlayer() {
        return player;
    }

    public Position getKeyInMazePosition() {
        return keyInMazePosition;
    }

    public Position getGunInMazePosition() {
        return gunInMazePosition;
    }
}
