package com.fin.maze.gameEvent;

import com.fin.Event;

public class InventoryEvent implements Event {
    private final Boolean key;
    private final Boolean gun;
    private final Boolean keyInMaze;
    private final Boolean gunInMaze;

    public InventoryEvent(Boolean key, Boolean gun, Boolean keyInMaze, Boolean gunInMaze) {
        this.key = key;
        this.gun = gun;
        this.keyInMaze = keyInMaze;
        this.gunInMaze = gunInMaze;
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
}
