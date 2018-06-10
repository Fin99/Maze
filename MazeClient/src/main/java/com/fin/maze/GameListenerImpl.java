package com.fin.maze;

import com.fin.connects.ConnectObserver;
import com.fin.connects.event.ServerEvent;
import com.fin.game.cover.Cover;
import com.fin.game.maze.Maze;
import com.fin.game.player.Item;
import com.fin.game.player.Player;
import com.fin.maze.gameEvent.*;
import com.fin.turn.TurnEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class GameListenerImpl<T> implements GameListener<ServerEvent> {
    //logger
    private final Logger logger = LogManager.getRootLogger();
    //

    private Cover cover;
    private List<Player> players;
    private Player ourPlayer;


    //process message from server
    @Override
    public void handle(ServerEvent serverEvent) {
        logger.info("Process ServerEvent...");
        //full update game
        if (serverEvent.getMessage().getType() != null && serverEvent.getMessage().getType().equals("Resize")) {
            logger.info("Start resize maze");
            saveStatement(serverEvent.getMessage().getMaze().getCover(), serverEvent.getMessage().getMaze().getPlayers(), serverEvent.getMessage().getMaze().getFirstPlayer());
            logger.info("Process resize maze. Chapter 1: creating and processing ResizeEvent, MazeEvent, Players Event");
            MazeObserver.processResizeEvent(new ResizeEvent(serverEvent.getMessage().getMaze().getSize()));
            MazeObserver.processMazeEvent(new MazeEvent(serverEvent.getMessage().getMaze().getCover()));
            MazeObserver.processPlayersEvent(new PlayersEvent(serverEvent.getMessage().getMaze().getPlayers()));
            logger.info("Process resize maze. Chapter 1 is finished.");
            logger.info("Process resize maze. Chapter 2: creating and processing InventoryEvent");
            Player player = serverEvent.getMessage().getMaze().getFirstPlayer();
            Maze maze = serverEvent.getMessage().getMaze();
            MazeObserver.processInventoryEvent(new InventoryEvent(
                    player.contains("Key"), player.contains("Gun"),
                    containsItem(maze.getItems(), "Key"), containsItem(maze.getItems(), "Gun")));
            logger.info("Process resize maze. Chapter 2 is finished.");
            logger.info("Process resize maze. Chapter 3: creating TurnEvent");
            logger.info("Start creating TurnEvent");
            if (serverEvent.getMessage().getMove() == null) {
                logger.fatal("ServerMessage.isMove() == null");
            }
            ConnectObserver.processTurnEvent(new TurnEvent(serverEvent.getMessage().getMove()));
            logger.info("TurnEvent(" + serverEvent.getMessage().getMove() + ") was creating");
            logger.info("Process resize maze. Chapter 3 is finished");
            logger.info("Finish resize maze");
            return;
        }
        //update playing field
        if (serverEvent.getMessage().getMaze().getCover() != null && (cover == null || !cover.equals(serverEvent.getMessage().getMaze().getCover()))) {
            logger.info("Start update maze");
            MazeObserver.processMazeEvent(new MazeEvent(serverEvent.getMessage().getMaze().getCover()));
            logger.info("Finish update maze");
        }
        //update players on playing field
        if (serverEvent.getMessage().getMaze().getPlayers() != null && (players == null || !playersEquals(players, (serverEvent.getMessage().getMaze().getPlayers())))) {
            logger.info("Start update players");
            MazeObserver.processPlayersEvent(new PlayersEvent(serverEvent.getMessage().getMaze().getPlayers()));
            logger.info("Finish update players");
        }
        //update icon key and gun on playing field and in bag our player
        if (serverEvent.getMessage().getMaze().getFirstPlayer() != null && (ourPlayer == null || !ourPlayer.equals(serverEvent.getMessage().getMaze().getFirstPlayer()))) {
            logger.info("Start update inventory");
            Player player = serverEvent.getMessage().getMaze().getFirstPlayer();
            Maze maze = serverEvent.getMessage().getMaze();
            MazeObserver.processInventoryEvent(new InventoryEvent(
                    player.contains("Key"), player.contains("Gun"),
                    containsItem(maze.getItems(), "Key"), containsItem(maze.getItems(), "Gun")));
            logger.info("Finish update inventory");
        }
        //notify what our player turn right now
        if (serverEvent.getMessage().getMove()) {
            logger.info("Start creating TurnEvent");
            if (serverEvent.getMessage().getMove() == null) {
                logger.fatal("ServerMessage.isMove() == null");
            }
            ConnectObserver.processTurnEvent(new TurnEvent(serverEvent.getMessage().getMove()));
            logger.info("TurnEvent(" + serverEvent.getMessage().getMove() + ") was creating");
        }
        //turn on animation shot
        if (serverEvent.getMessage().getType() != null && serverEvent.getMessage().getType().equals("Shot")) {
            logger.info("Start shot");
            MazeObserver.processShotEvent(new ShotEvent(
                    serverEvent.getMessage().getStartPosition(), serverEvent.getMessage().getFinishPosition(),
                    serverEvent.getMessage().getDirection()));
            logger.info("Finish shot");
        }
        //turn on animation move
        if (serverEvent.getMessage().getType() != null && serverEvent.getMessage().getType().equals("Move")) {
            logger.info("Start move player");
            MazeObserver.processMoveEvent(new MoveEvent(
                    serverEvent.getMessage().getType(),
                    serverEvent.getMessage().getPlayer(),
                    serverEvent.getMessage().getStartPosition(), serverEvent.getMessage().getFinishPosition(),
                    serverEvent.getMessage().getDirection()));
            logger.info("Finish move player");
        }
        //todo add TextEvent associated with infLabel
        saveStatement(serverEvent.getMessage().getMaze().getCover(), serverEvent.getMessage().getMaze().getPlayers(), serverEvent.getMessage().getMaze().getFirstPlayer());
        logger.info("Statement was update. Process ServerEvent is finished");
    }

    private boolean playersEquals(List<Player> players, List<Player> players1) {
        logger.info("Start players comparison");
        if (players == null || players1 == null) {
            logger.info("");
            return false;
        }
        if (players.size() != players1.size()) return false;
        for (int i = 0; i < players.size(); i++) {
            if (!players.get(i).equals(players1.get(i))) return false;
        }
        return true;
    }

    //check contains item with that name in this list
    private boolean containsItem(List<Item> items, String name) {
        for (Item i : items) {
            if (i.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private void saveStatement(Cover cover, List<Player> players, Player ourPlayer) {
        if (cover != null) this.cover = cover;
        if (players != null) this.players = players;
        if (ourPlayer != null) this.ourPlayer = ourPlayer;
    }
}
