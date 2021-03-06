package com.fin.game.cover;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class Cover implements Serializable {
    private Set<Field> cov;
    private int size;

    public Cover(int size) {
        this.size = size;
        cov = new HashSet<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cov.add(new Field(i, j));
            }
        }
    }

    public Cover(Set<Field> cov) {
        this.cov = cov;
        size = (int) Math.sqrt(cov.size());
    }

    public void addWall(int x, int y, Direction wall) {
        for (Field field : cov) {
            if (field.getX() == x && field.getY() == y) field.addWalls(wall);
        }
    }

    public boolean containsWall(int x, int y, Direction wall) {
        for (Field field : cov) {
            if (field.getX() == x && field.getY() == y && field.containsWall(wall)) return true;
        }
        return false;
    }

    public Set<Field> getCov() {
        return cov;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cover)) return false;
        Cover cover = (Cover) o;
        if (size != cover.size) return false;
        if (cov.size() != cover.getCov().size()) return false;
        Iterator<Field> covI = cov.iterator();
        Iterator<Field> coverI = cover.getCov().iterator();
        while (covI.hasNext()) {
            if (!covI.next().equals(coverI.next())) return false;
        }
        return true;
    }
}
