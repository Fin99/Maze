package com.fin.controllers.processButtonAction;

import com.fin.connects.server.ConnectServerObserver;
import com.fin.connects.server.event.RestartGameEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class RestartGameButtonActionListeners implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent event) {
        ConnectServerObserver.processRestartGameEvent(new RestartGameEvent());
    }
}
