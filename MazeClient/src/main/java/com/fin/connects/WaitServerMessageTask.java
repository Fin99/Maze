package com.fin.connects;

import com.fin.ServerMessage;
import com.fin.connects.event.ReplacementConnectEvent;
import com.fin.connects.event.ServerEvent;
import com.fin.maze.MazeObserver;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WaitServerMessageTask extends Task<ServerMessage> implements ReplacementConnectListener, EventHandler<WorkerStateEvent> {
    //logger
    private final Logger logger = LogManager.getRootLogger();
    //
    private Connect server;

    {
        setOnSucceeded(this);
    }

    @Override
    public ServerMessage call() throws Exception {
        logger.info("WaitServerMessageTask is launched");
        return (ServerMessage) server.waitResponse();
    }

    @Override
    public void handle(ReplacementConnectEvent replacementConnectEvent) {
        logger.info("(WaitServerMessageTask)Process ReplacementConnectEvent...");
        server = replacementConnectEvent.getConnect();
        logger.info("(WaitServerMessageTask)Link on server changed. Process is finished");
    }

    //restart Task
    @Override
    public void handle(WorkerStateEvent event) {
        logger.info("Processing of the received message...");
        ExecutorService service = Executors.newFixedThreadPool(1);
        service.submit(new WaitServerMessageTask());
        logger.info("Create new ServerEvent");
        MazeObserver.processServerEvent(new ServerEvent((ServerMessage) event.getSource()));
        logger.info("Process of the received message is passed on");
    }
}
