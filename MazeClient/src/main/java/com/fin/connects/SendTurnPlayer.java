package com.fin.connects;

import com.fin.ClientMessage;
import com.fin.connects.event.ReplacementConnectEvent;
import com.fin.game.cover.Direction;
import com.fin.turn.TurnEvent;
import com.fin.turn.TurnListener;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SendTurnPlayer implements ReplacementConnectListener, TurnListener, EventHandler<KeyEvent> {
    //logger
    private final Logger logger = LogManager.getRootLogger();
    //
    private Connect server;
    private boolean move;

    @Override
    public void handle(ReplacementConnectEvent replacementConnectEvent) {
        logger.info("(SendTurnPlayers)Process ReplacementConnectEvent...");
        server = replacementConnectEvent.getConnect();
        logger.info("(SendTurnPlayers)Link on server changed. Process is finished");
    }

    @Override
    public void handle(TurnEvent turnEvent) {
        logger.info("Processing of the TurnEvent...");
        //todo set Timer while message not send or if timer is stopped then send com.fin.ClientMessage("Move", null)
        move = turnEvent.isMove();
        logger.info("Move(boolean) = " + move + ". Processing of the TurnEvent is finished");
    }

    //listen key
    @Override
    public void handle(KeyEvent event) {
        logger.info("Processing of the KeyEvent...");
        ClientMessage clientMessage = null;
        if (move) {
            logger.info("Check. Is pressed key valid?");
            move = false;
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
            server.sendRequest(clientMessage);
            logger.info("Message from server sent. Properties: type - " + clientMessage.getType() + ", direction - " + clientMessage.getDirection().name());
        }
    }
}
