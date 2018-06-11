package com.fin.connects;

import com.fin.connects.event.ReplacementConnectEvent;
import com.fin.connects.event.RestartGameEvent;
import com.fin.turn.TurnEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ConnectObserver {
    //logger
    private static final Logger logger = LogManager.getRootLogger();
    //
    private static List<ReplacementConnectListener> listeners;
    private static SendTurnPlayer sendTurnPlayer;
    static {
        listeners = new ArrayList<>();
    }
    
    public static void addSendTurnPlayer(SendTurnPlayer sendTurnPlayer){
        ConnectObserver.sendTurnPlayer = sendTurnPlayer;
        listeners.add(sendTurnPlayer);
        logger.info("SendTurnPlayer is init");
    }
    
    public static void addWaitServerMessageTask(WaitServerMessageTask waitServerMessageTask){
        listeners.add(waitServerMessageTask);
        logger.info("WaitServerMessageTask is init");
    }
    
    public static void processReplacementConnectEvent(ReplacementConnectEvent event){
        logger.info("Processing of the ReplacementConnectEvent is started");
        for(ReplacementConnectListener listener : listeners){
            listener.handle(event);
        }

    }
    
    public static void processTurnEvent(TurnEvent event){
        logger.info("Processing of the TurnEvent is started");
        sendTurnPlayer.handle(event);
    }

    public static void processRestartGameEvent(RestartGameEvent event){
        logger.info("Proccessing of the RestartGameEvent is started");
        sendTurnPlayer.handle(event);
    }
}

