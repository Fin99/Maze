package com.fin.connects.database;

import com.fin.connects.database.event.AuthorizationEvent;
import com.fin.connects.database.event.RegistrationEvent;
import com.fin.connects.database.event.ResponseAuthorizationEvent;
import com.fin.connects.database.event.ResponseRegistrationEvent;
import com.fin.entity.User;
import com.fin.lorm.ConnectionDefaultImpl;
import com.fin.lorm.Statement;
import com.fin.lorm.StatementDefaultImpl;
import com.fin.maze.MazeObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

public class ConnectDatabaseObserver {
    //logger
    private static final Logger logger = LogManager.getRootLogger();
    //
    private static Statement<User, Integer> statement;

    static {
        try {
            statement = new StatementDefaultImpl<>(new ConnectionDefaultImpl("jdbc:postgresql://127.0.0.1:2600/studs", "s243872", "yvs787"), User.class, Integer.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void processRegistration(RegistrationEvent event) {
        logger.info("Registration user...");
        boolean result = false;
        try {
            if (statement.get(event.getUser()).size() > 0) {
                MazeObserver.processResponseRegistrationEvent(new ResponseRegistrationEvent(false));
            } else {
                result = statement.save(event.getUser());
            }
        } catch (IllegalAccessException | SQLException | InstantiationException e) {
            logger.error("Catch exception when registration user");
            MazeObserver.processResponseRegistrationEvent(new ResponseRegistrationEvent(false));
        }
        MazeObserver.processResponseRegistrationEvent(new ResponseRegistrationEvent(result));
        logger.info("Registration user is finished");
    }

    public static void processAuthorization(AuthorizationEvent event) {
        logger.info("Authorization user...");
        boolean result = false;
        try {
            result = statement.get(event.getUser()).size() > 0;
        } catch (IllegalAccessException | SQLException | InstantiationException e) {
            logger.error("Catch exception when authorization user");
            MazeObserver.processResponseAuthorizationEvent(new ResponseAuthorizationEvent(false));
        }
        MazeObserver.processResponseAuthorizationEvent(new ResponseAuthorizationEvent(result));
        logger.info("Authorization user is finished");
    }
}
