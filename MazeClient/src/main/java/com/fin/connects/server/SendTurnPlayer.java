package com.fin.connects.server;

import com.fin.ClientMessage;
import com.fin.connects.server.event.ReplacementConnectEvent;
import com.fin.connects.server.event.RestartGameEvent;
import com.fin.game.cover.Direction;
import com.fin.maze.MazeObserver;
import com.fin.maze.gameEvent.TickEvent;
import com.fin.turn.TurnEvent;
import com.fin.turn.TurnListener;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SendTurnPlayer implements ReplacementConnectListener, TurnListener, EventHandler, RestartGameListener {
    //logger
    private final Logger logger = LogManager.getRootLogger();
    //
    private Connect server;
    private boolean move;
    private int counter;
    private Tick tickNow;

    @Override
    public void handle(ReplacementConnectEvent replacementConnectEvent) {
        logger.info("(SendTurnPlayers)Process ReplacementConnectEvent...");
        server = replacementConnectEvent.getConnect();
        logger.info("(SendTurnPlayers)Link on server changed. Process is finished");
    }

    @Override
    public void handle(TurnEvent turnEvent) {
        logger.info("Processing of the TurnEvent...");
        move = turnEvent.isMove();
        if (move) {
            counter = 20;
            ExecutorService service = Executors.newFixedThreadPool(1);
            tickNow = new Tick();
            service.submit(tickNow);
        } else {
            MazeObserver.processTickEvent(new TickEvent(0, false));
        }
        logger.info("Move(boolean) = " + move + ". Processing of the TurnEvent is finished");
    }

    @Override
    public void handle(RestartGameEvent event) {
        logger.info("Processing of the RestartGameEvent");
        server.sendRequest(new ClientMessage("Restart", null));
        logger.info("Processing of the RestartGameEvent is finished");
    }

    //listen key
    @Override
    public void handle(javafx.event.Event event1) {
        if (event1 instanceof KeyEvent) {
            KeyEvent event = (KeyEvent) event1;
            logger.info("Processing of the KeyEvent...");
            ClientMessage clientMessage = null;
            if (move) {
                logger.info("Check. Is pressed key valid?");
                switch (event.getCode()) {
                    case RIGHT:
                        clientMessage = new ClientMessage("Shot", Direction.RIGHT);
                        break;
                    case LEFT:
                        clientMessage = new ClientMessage("Shot", Direction.LEFT);
                        break;
                    case UP:
                        clientMessage = new ClientMessage("Shot", Direction.UP);
                        break;
                    case DOWN:
                        clientMessage = new ClientMessage("Shot", Direction.DOWN);
                        break;
                    case W:
                        clientMessage = new ClientMessage("Move", Direction.UP);
                        break;
                    case A:
                        clientMessage = new ClientMessage("Move", Direction.LEFT);
                        break;
                    case S:
                        clientMessage = new ClientMessage("Move", Direction.DOWN);
                        break;
                    case D:
                        clientMessage = new ClientMessage("Move", Direction.RIGHT);
                        break;
                }
            } else {
                logger.info("Player can`t move now");
            }
            if (clientMessage != null) {
                move = false;
                server.sendRequest(clientMessage);
                MazeObserver.processTickEvent(new TickEvent(0, false));
                logger.info("Message from server sent. Properties: type - " + clientMessage.getType() + ", direction - " + clientMessage.getDirection().name());
            }
        } else {
            if (((WorkerStateEvent) event1).getSource().equals(tickNow) && move) {
                MazeObserver.processTickEvent(new TickEvent(counter--, move));
                if (counter == -1) {
                    logger.info("Player pass his turn");
                    move = false;
                    server.sendRequest(new ClientMessage("Pass", null));
                    MazeObserver.processTickEvent(new TickEvent(0, false));
                } else {
                    ExecutorService service = Executors.newFixedThreadPool(1);
                    tickNow = new Tick();
                    service.submit(tickNow);
                    logger.info("Restart tick");
                }
            } else {
                MazeObserver.processTickEvent(new TickEvent(counter--, move));
            }
        }
    }

    private class Tick extends Task<Void> {
        {
            setOnSucceeded(SendTurnPlayer.this);
        }

        @Override
        public Void call() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
