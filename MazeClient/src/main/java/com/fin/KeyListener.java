package com.fin;

import com.fin.game.cover.Direction;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;


public class KeyListener implements EventHandler<KeyEvent> {

    public KeyListener() {
    }

    @Override
    public void handle(KeyEvent event) {
        if (ServerMessageHandler.messageFlag) {
            switch (event.getCode()) {
                case RIGHT:
                    Connect.sendRequest("Shot", Direction.RIGHT);
                    break;
                case LEFT:
                    Connect.sendRequest("Shot", Direction.LEFT);
                    break;
                case UP:
                    Connect.sendRequest("Shot", Direction.UP);
                    break;
                case DOWN:
                    Connect.sendRequest("Shot", Direction.DOWN);
                    break;
                case W:
                    Connect.sendRequest("Move", Direction.UP);
                    break;
                case A:
                    Connect.sendRequest("Move", Direction.LEFT);
                    break;
                case S:
                    Connect.sendRequest("Move", Direction.DOWN);
                    break;
                case D:
                    Connect.sendRequest("Move", Direction.RIGHT);
                    break;
            }
        }
    }
}

