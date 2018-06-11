package com.fin.game.player;

import java.io.Serializable;

public class Item implements Serializable, Position {
    private String name;
    private int x;
    private int y;

    public Item(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public Position getPosition(){
        return new Position() {
            int x = Item.this.x;
            int y = Item.this.y;

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

    public String getName() {
        return name;
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
}
