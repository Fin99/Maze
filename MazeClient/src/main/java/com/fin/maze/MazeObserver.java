package com.fin.maze;

import com.fin.connects.database.event.ResponseAuthorizationEvent;
import com.fin.connects.database.event.ResponseRegistrationEvent;
import com.fin.connects.server.event.ServerEvent;
import com.fin.controllers.MazeController;
import com.fin.maze.gameEvent.*;
import com.fin.maze.localEvent.LocalEvent;
import com.fin.maze.localHandlers.LocalHandler;
import com.fin.maze.loginListener.ResponseAuthorizationEventHandler;
import com.fin.maze.loginListener.ResponseRegistrationEventHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class MazeObserver {
    //logger
    private static final Logger logger = LogManager.getRootLogger();
    //
    private static GameListener<ServerEvent> gameListener;
    private static MazeController mazeController;
    private static ResponseAuthorizationEventHandler authorizationEventHandler;
    private static ResponseRegistrationEventHandler registrationEventHandler;
    private static List<LocalHandler> localHandlers;

    static {
        localHandlers = new ArrayList<>();
    }

    public static void addMazeListener(MazeController controller) {
        mazeController = controller;
        localHandlers.add(controller);
        logger.info("MazeListener was initialized");
    }

    public static void addGameListener(GameListener<ServerEvent> gameListener) {
        MazeObserver.gameListener = gameListener;
        logger.info("GameListener was initialized");
    }

    public static void addResponseAuthorizationEventHandler(ResponseAuthorizationEventHandler handler) {
        MazeObserver.authorizationEventHandler = handler;
        logger.info("ResponseAuthorizationEventHandler was initialized");
    }

    public static void addResponseRegistrationEventHandler(ResponseRegistrationEventHandler handler) {
        MazeObserver.registrationEventHandler = handler;
        logger.info("ResponseRegistrationEventHandler was initialized");
    }

    public static void addLocaleHandler(LocalHandler handler) {
        localHandlers.add(handler);
        logger.info("Added new LocalHandler");
    }

    public static synchronized void processResponseAuthorizationEvent(ResponseAuthorizationEvent event) {
        logger.info("Process ResponseAuthorizationEvent is started");
        authorizationEventHandler.handle(event);
        logger.info("Process ResponseAuthorizationEvent is finished");
    }

    public static strictfp void processLocaleEvent(LocalEvent event) {
        logger.info("Process ResponseAuthorizationEvent is started");
        for (LocalHandler lH : localHandlers) {
            lH.handle(event);
        }
        logger.info("Process ResponseAuthorizationEvent is finished");
    }

    public static synchronized void processResponseRegistrationEvent(ResponseRegistrationEvent event) {
        logger.info("Process ResponseRegistrationEvent is started");
        registrationEventHandler.handle(event);
        logger.info("Process ResponseRegistrationEvent is finished");
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

    public static synchronized void processEndGameEvent(EndGameEvent event) {
        logger.info("Process EndGameEvent is started");
        mazeController.handle(event);
        logger.info("Process EndGameEvent is finished");
    }
}
