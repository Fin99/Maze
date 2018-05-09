package com.fin.game.maze;

import com.fin.game.cover.Cover;
import com.fin.game.cover.Direction;
import com.fin.game.player.Item;
import com.fin.game.player.Player;

import java.net.Socket;
import java.util.List;

public interface Maze {
    Maze start(int x, int y);
    Maze go(Direction direction, int idPlayer);
    void setCover(Cover cover);
    int getSize();
    void shot(int idPlayer, Direction direction);
    void deletePlayer(int idPlayer);
    Player getFirstPlayer();
    List<Player> getPlayers() ;

    List<Item> getItems() ;
}
