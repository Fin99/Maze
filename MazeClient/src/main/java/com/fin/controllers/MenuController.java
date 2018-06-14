package com.fin.controllers;

import com.fin.Windows1251Control;
import com.fin.controllers.processButtonAction.ReconnectServerButtonActionListeners;
import com.fin.controllers.processButtonAction.RestartGameButtonActionListeners;
import com.fin.maze.MazeObserver;
import com.fin.maze.localEvent.LocalEvent;
import com.fin.maze.localHandlers.LocalHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable, LocalHandler {
    //logger
    private final Logger logger = LogManager.getRootLogger();
    //
    @FXML
    AnchorPane root;
    @FXML
    Button restartGame;
    @FXML
    Button reconnectServer;
    @FXML
    Button exit;
    private ResourceBundle resourceBundle;

    {
        MazeObserver.addLocaleHandler(this);
        resourceBundle = ResourceBundle.getBundle("strings", new Windows1251Control());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        exit.setText(resourceBundle.getString("exit"));
        restartGame.setText(resourceBundle.getString("new_games"));
        reconnectServer.setText(resourceBundle.getString("search_server"));
    }

    public void init(Stage menuStage) {
        logger.info("Start MenuController.init()...");

        restartGame.setPrefHeight(root.getPrefHeight() / 3);
        restartGame.setPrefWidth(root.getPrefWidth());
        restartGame.setLayoutX(0);
        restartGame.setLayoutY(0);
        logger.info("New size for restartGame(Button) was installed");
        restartGame.setOnAction(new RestartGameButtonActionListeners());
        logger.info("New listener was installed for restartGame(Button)");

        exit.setPrefHeight(root.getPrefHeight() / 3);
        exit.setPrefWidth(root.getPrefWidth());
        exit.setLayoutX(0);
        exit.setLayoutY(root.getPrefHeight() / 3 * 2);
        logger.info("New size for exit (Button) was installed");
        exit.setOnAction(event1 -> {
            logger.info("Process new ExitAction...");
            System.exit(0);
        });
        logger.info("New listener was installed for exit(Button)");

        reconnectServer.setPrefHeight(root.getPrefHeight() / 3);
        reconnectServer.setPrefWidth(root.getPrefWidth());
        reconnectServer.setLayoutX(0);
        reconnectServer.setLayoutY(root.getPrefHeight() / 3);
        logger.info("New size for reconnectServer (Button) was installed");
        reconnectServer.setOnAction(new ReconnectServerButtonActionListeners(root, menuStage));
        logger.info("New listener was installed for reconnectServer(Button)");
    }

    @Override
    public void handle(LocalEvent localEvent) {
        resourceBundle = ResourceBundle.getBundle("strings", localEvent.getLocale(), new Windows1251Control());
        exit.setText(resourceBundle.getString("exit"));
        restartGame.setText(resourceBundle.getString("new_games"));
        reconnectServer.setText(resourceBundle.getString("search_server"));
    }
}
