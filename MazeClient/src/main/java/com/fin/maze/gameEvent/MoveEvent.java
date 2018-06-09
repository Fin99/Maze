package com.fin.maze.gameEvent;

import com.fin.Event;
import com.fin.game.cover.Direction;
import com.fin.game.player.Player;
import com.fin.game.player.Position;

public class MoveEvent implements Event {
    private final String type;
    private final Player player;
    private final Position start;
    private final Position finish;
    private final Direction direction;

    public MoveEvent(String type, Player player, Position start, Position finish, Direction direction) {
        this.type = type;
        this.player = player;

        this.start = start;
        this.finish = finish;
        this.direction = direction;
    }

    public Player getPlayer() {
        return player;
    }

    public String getType() {
        return type;
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
