package com.fin.maze;

import com.fin.connects.event.ServerEvent;
import com.fin.controllers.MazeController;
import com.fin.maze.gameEvent.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MazeObserver {
    //logger
    private static final Logger logger = LogManager.getRootLogger();
    //
    private static GameListener<ServerEvent> gameListener;
    private static MazeController mazeController;

    public static void addMazeListener(MazeController controller) {
        mazeController = controller;
        logger.info("MazeListener was initialized");
    }

    public static void addGameListener(GameListener<ServerEvent> gameListener) {
        MazeObserver.gameListener = gameListener;
        logger.info("GameListener was initialized");
    }

    public static synchronized void processServerEvent(ServerEvent event) {
        logger.info("Process ServerEvent is started");
        gameListener.handle(event);
        logger.info("Process ServerEvent is finished");
    }

    public static synchronized void processInventoryEvent(InventoryEvent event) {
        logger.info("Process InventoryEvent is started");
        mazeController.handle(event);
        logger.info("Process InventoryEvent is finished");
    }

    public static synchronized void processMazeEvent(MazeEvent event) {
        logger.info("Process MazeEvent is started");
        mazeController.handle(event);
        logger.info("Process MazeEvent is finished");
    }

    public static synchronized void processMoveEvent(MoveEvent event) {
        logger.info("Process MoveEvent is started");
        mazeController.handle(event);
        logger.info("Process MoveEvent is finished");
    }

    public static synchronized void processPlayersEvent(PlayersEvent event) {
        logger.info("Process PlayersEvent is started");
        mazeController.handle(event);
        logger.info("Process PlayersEvent is finished");
    }

    public static synchronized void processResizeEvent(ResizeEvent event) {
        logger.info("Process ResizeEvent is started");
        mazeController.handle(event);
        logger.info("Process ResizeEvent is finished");
    }

    public static synchronized void processShotEvent(ShotEvent event) {
        logger.info("Process ShotEvent is started");
        mazeController.handle(event);
        logger.info("Process ShotEvent is finished");
    }
}
