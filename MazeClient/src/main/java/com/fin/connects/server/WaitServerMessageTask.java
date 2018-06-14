package com.fin.connects.server;

import com.fin.ServerMessage;
import com.fin.connects.server.event.ReplacementConnectEvent;
import com.fin.connects.server.event.ServerEvent;
import com.fin.maze.MazeObserver;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WaitServerMessageTask implements ReplacementConnectListener, EventHandler<WorkerStateEvent> {
    //logger
    private final Logger logger = LogManager.getRootLogger();
    //
    private Connect server;


    @Override
    public void handle(ReplacementConnectEvent replacementConnectEvent) {
        logger.info("(WaitServerMessageTask)Process ReplacementConnectEvent...");
        server = replacementConnectEvent.getConnect();
        logger.info("(WaitServerMessageTask)Link on server changed. Process is finished");
        ExecutorService service = Executors.newFixedThreadPool(1);
        service.submit(new Waiting());
    }

    //restart Task
    @Override
    public void handle(WorkerStateEvent event) {
        logger.info("Processing of the received message...");
        logger.info("Create new ServerEvent");
        ServerMessage message = (ServerMessage) event.getSource().getValue();
        if(message==null){
            logger.fatal("Received message is null");
        }
        MazeObserver.processServerEvent(new ServerEvent(message));
        logger.info("Restart waiting");
        ExecutorService service = Executors.newFixedThreadPool(1);
        service.submit(new Waiting());
    }

    private class Waiting extends Task<ServerMessage> {
        @Override
        public ServerMessage call() throws Exception {
            logger.info("WaitServerMessageTask is launched");
            return (ServerMessage) server.waitResponse();
        }

        {
            setOnSucceeded(WaitServerMessageTask.this);
        }
    }
}
