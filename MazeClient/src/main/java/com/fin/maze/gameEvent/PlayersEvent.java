package com.fin.maze.gameEvent;

import com.fin.Event;
import com.fin.game.player.Player;

import java.util.List;

public class PlayersEvent implements Event {
    private final List<Player> players;

    public PlayersEvent(List<Player> players) {
        this.players = players;
    }

    public List<Player> getPlayers() {
        return players;
    }
}
