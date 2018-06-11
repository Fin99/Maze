package com.fin.controllers.processButtonAction;

import com.fin.connects.ConnectObserver;
import com.fin.connects.event.RestartGameEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class RestartGameButtonActionListeners implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent event) {
        ConnectObserver.processRestartGameEvent(new RestartGameEvent());
    }
}
