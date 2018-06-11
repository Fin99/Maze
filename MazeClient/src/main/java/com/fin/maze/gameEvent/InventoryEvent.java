package com.fin.maze.gameEvent;

import com.fin.Event;
import com.fin.game.player.Position;

public class InventoryEvent implements Event {
    private final Boolean key;
    private final Boolean gun;
    private final Boolean keyInMaze;
    private final Boolean gunInMaze;
    private final Position keyInMazePosition;
    private final Position gunInMazePosition;
    public InventoryEvent(Boolean key, Boolean gun, Boolean keyInMaze, Boolean gunInMaze, Position keyInMazePosition, Position gunInMazePosition) {
        this.key = key;
        this.gun = gun;
        this.keyInMaze = keyInMaze;
        this.gunInMaze = gunInMaze;
        this.keyInMazePosition = keyInMazePosition;
        this.gunInMazePosition = gunInMazePosition;
    }

    public Boolean getKeyInMaze() {
        return keyInMaze;
    }

    public Boolean getGunInMaze() {
        return gunInMaze;
    }

    public Boolean getKey() {
        return key;
    }

    public Boolean getGun() {
        return gun;
    }

    public Position getKeyInMazePosition() {
        return keyInMazePosition;
    }

    public Position getGunInMazePosition() {
        return gunInMazePosition;
    }
}
