package com.fin;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ServerMessageService extends Service<ServerMessage> {

    private Connect server;

    public ServerMessageService(Connect server) {
        this.server = server;
    }

    @Override
    public Task<ServerMessage> createTask() {
        return new ServerMessageTask(server);
    }
}
