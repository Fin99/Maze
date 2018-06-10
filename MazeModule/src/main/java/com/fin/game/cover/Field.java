package com.fin.game.cover;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Field)) return false;
        Field field = (Field) o;
        if (getX() != field.getX() || getY() != field.getY()) return false;
        if (walls.size() != field.walls.size()) return false;
        Iterator<Direction> wallsI = walls.iterator();
        Iterator<Direction> fieldWallsI = field.walls.iterator();
        while (wallsI.hasNext()) {
            if (!wallsI.next().equals(fieldWallsI.next())) return false;
        }
        return true;
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
