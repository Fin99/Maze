package com.fin;

import com.fin.game.cover.Direction;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;


public class KeyListener implements EventHandler<KeyEvent> {
    private Connect server;

    public KeyListener(Connect server) {
        this.server = server;
    }

    @Override
    public void handle(KeyEvent event) {
        if (ServerMessageHandler.messageFlag) {
            switch (event.getCode()) {
                case RIGHT:
                    server.sendRequest(true, Direction.RIGHT);
                    break;
                case LEFT:
                    server.sendRequest(true, Direction.LEFT);
                    break;
                case UP:
                    server.sendRequest(true, Direction.UP);
                    break;
                case DOWN:
                    server.sendRequest(true, Direction.DOWN);
                    break;
                case W:
                    server.sendRequest(false, Direction.UP);
                    break;
                case A:
                    server.sendRequest(false, Direction.LEFT);
                    break;
                case S:
                    server.sendRequest(false, Direction.DOWN);
                    break;
                case D:
                    server.sendRequest(false, Direction.RIGHT);
                    break;
            }
        }
    }
}

