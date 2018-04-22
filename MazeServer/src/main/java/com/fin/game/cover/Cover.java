package com.fin.game.cover;

import java.util.HashSet;
import java.util.Set;

public class Cover {
    Set<Field> cov;
    int size;

    public Cover(int size) {
        this.size = size;
        cov = new HashSet<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cov.add(new Field(i, j));
            }
        }
    }

    public void addWall(int x, int y, Wall wall) {
        for (Field field : cov) {
            if (field.getX() == x && field.getY() == y) field.addWalls(wall);
        }
    }

    public boolean containsWall(int x, int y, Wall wall) {
        for (Field field : cov) {
            if (field.getX() == x && field.getY() == y && field.containsWall(wall)) return true;
        }
        return false;
    }
}
