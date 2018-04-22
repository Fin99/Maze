package com.fin.game.cover;

import java.util.HashSet;
import java.util.Set;

public class Field {
    private Set<Wall> walls;
    private int x;
    private int y;

    public Field(int x, int y) {
        this.x = x;
        this.y = y;
        walls = new HashSet<>();
    }

    public void addWalls(Wall wall){
        walls.add(wall);
    }

    public boolean containsWall(Wall wall){
        return walls.contains(wall);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
