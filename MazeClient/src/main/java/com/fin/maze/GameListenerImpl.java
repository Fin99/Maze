package com.fin.maze;

import com.fin.ServerMessage;
import com.fin.connects.ConnectObserver;
import com.fin.connects.event.ServerEvent;
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
    private ServerMessage oldMessage;

    //process message from server
    @Override
    public void handle(ServerEvent serverEvent) {
        logger.info("Process serverEvent...");
        //full update game
        if (serverEvent.getMessage().getType().equals("Resize")) {
            logger.info("Start resize maze");
            oldMessage = serverEvent.getMessage();
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
            logger.info("Finish resize maze");
            return;
        }
        //update playing field
        if (oldMessage == null || !serverEvent.getMessage().getMaze().getCover().equals(oldMessage.getMaze().getCover())) {
            logger.info("Start update maze");
            MazeObserver.processMazeEvent(new MazeEvent(serverEvent.getMessage().getMaze().getCover()));
            logger.info("Finish update maze");
        }
        //update players on playing field
        if (oldMessage == null || !serverEvent.getMessage().getMaze().getPlayers().equals(oldMessage.getMaze().getPlayers())) {
            logger.info("Start update players");
            MazeObserver.processPlayersEvent(new PlayersEvent(serverEvent.getMessage().getMaze().getPlayers()));
            logger.info("Finish update players");
        }
        //update icon key and gun on playing field and in bag our player
        if (oldMessage == null || !serverEvent.getMessage().getMaze().getFirstPlayer().equals(oldMessage.getMaze().getFirstPlayer())) {
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
        if (serverEvent.getMessage().getType().equals("Shot")) {
            logger.info("Start shot");
            MazeObserver.processShotEvent(new ShotEvent(
                    serverEvent.getMessage().getStartPosition(), serverEvent.getMessage().getFinishPosition(),
                    serverEvent.getMessage().getDirection()));
            logger.info("Finish shot");
        }
        //turn on animation move
        if (serverEvent.getMessage().getType().equals("Move")) {
            logger.info("Start move player");
            MazeObserver.processMoveEvent(new MoveEvent(
                    serverEvent.getMessage().getType(),
                    serverEvent.getMessage().getPlayer(),
                    serverEvent.getMessage().getStartPosition(), serverEvent.getMessage().getFinishPosition(),
                    serverEvent.getMessage().getDirection()));
            logger.info("Finish move player");
        }
        //todo add TextEvent associated with infLabel
        oldMessage = serverEvent.getMessage();
        logger.info("OldMessage was update. Process ServerEvent is finished");
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
}
