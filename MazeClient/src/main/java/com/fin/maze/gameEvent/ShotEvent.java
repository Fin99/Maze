package com.fin.maze.gameEvent;

import com.fin.Event;
import com.fin.game.cover.Direction;
import com.fin.game.player.Position;

public class ShotEvent implements Event {
    private final Position start;
    private final Position finish;
    private final Direction direction;

    public ShotEvent(Position start, Position finish, Direction direction) {
        this.start = start;
        this.finish = finish;
        this.direction = direction;

    }

    public Position getStart() {
        return start;
    }

    public Position getFinish() {
        return finish;
    }

    public Direction getDirection() {
        return direction;
    }
}
