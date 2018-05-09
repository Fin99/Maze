package com.fin.game.cover;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Field implements Serializable {
    private Set<Direction> walls;
    private int x;
    private int y;

    public Field(int x, int y) {
        this.x = x;
        this.y = y;
        walls = new HashSet<>();
    }

    public void addWalls(Direction wall){
        walls.add(wall);
    }

    public boolean containsWall(Direction wall){
        return walls.contains(wall);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
