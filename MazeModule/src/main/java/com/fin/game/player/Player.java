package com.fin.game.player;

import com.fin.game.cover.Cover;

import java.io.Serializable;
import java.util.Objects;

public class Player implements Serializable, Position {
    private int x;
    private int y;
    private Backpack backpack;
    private Cover stepList;
    private int id;

    public Player(int x, int y, int size, int id) {
        this.x = x;
        this.y = y;
        stepList = new Cover(size);
        backpack = new Backpack();
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Position getPosition(){
        return new Position() {
            int x = Player.this.x;
            int y = Player.this.y;
            @Override
            public int getX() {
                return x;
            }

            @Override
            public void setX(int x) {

            }

            @Override
            public int getY() {
                return y;
            }

            @Override
            public void setY(int y) {

            }
        };
    }

    public void add(Item item) {
        backpack.add(item);
    }

    public void remove(String item) {
        backpack.remove(item);
    }

    public boolean contains(String item) {
        return backpack.contains(item);
    }

    public Cover getStepList() {
        return stepList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return getX() == player.getX() &&
                getY() == player.getY() &&
                getId() == player.getId() &&
                Objects.equals(backpack, player.backpack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY(), getId());
    }
}
