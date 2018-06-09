package com.fin;

import com.fin.game.cover.Direction;

import java.io.Serializable;

public class ClientMessage implements Serializable {
    private String type;
    private Direction direction;

    public ClientMessage(String type, Direction direction) {

        this.type = type;
        this.direction = direction;
    }

    public String getType() {
        return type;
    }

    public Direction getDirection() {
        return direction;
    }
}
