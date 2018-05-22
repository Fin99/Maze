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
                    if(ServerMessageHandler.time!=null)ServerMessageHandler.time[0] = 10;
                    break;
                case LEFT:
                    Connect.sendRequest("Shot", Direction.LEFT);
                    if(ServerMessageHandler.time!=null)ServerMessageHandler.time[0] = 10;
                    break;
                case UP:
                    Connect.sendRequest("Shot", Direction.UP);
                    if(ServerMessageHandler.time!=null)ServerMessageHandler.time[0] = 10;
                    break;
                case DOWN:
                    Connect.sendRequest("Shot", Direction.DOWN);
                    if(ServerMessageHandler.time!=null)ServerMessageHandler.time[0] = 10;
                    break;
                case W:
                    Connect.sendRequest("Move", Direction.UP);
                    if(ServerMessageHandler.time!=null)ServerMessageHandler.time[0] = 10;
                    break;
                case A:
                    Connect.sendRequest("Move", Direction.LEFT);
                    if(ServerMessageHandler.time!=null)ServerMessageHandler.time[0] = 10;
                    break;
                case S:
                    Connect.sendRequest("Move", Direction.DOWN);
                    if(ServerMessageHandler.time!=null)ServerMessageHandler.time[0] = 10;
                    break;
                case D:
                    Connect.sendRequest("Move", Direction.RIGHT);
                    if(ServerMessageHandler.time!=null)ServerMessageHandler.time[0] = 10;
                    break;
            }
        }
    }
}

