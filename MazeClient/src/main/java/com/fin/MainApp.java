package com.fin;

import com.fin.connects.server.ConnectServerObserver;
import com.fin.connects.server.SendTurnPlayer;
import com.fin.connects.server.WaitServerMessageTask;
import com.fin.controllers.AuthorizationController;
import com.fin.controllers.MazeController;
import com.fin.maze.GameListenerImpl;
import com.fin.maze.MazeObserver;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class MainApp extends Application {
    //logger
    private final Logger logger = LogManager.getRootLogger();
    //
    private Stage primaryStage;
    private MazeController rootLayoutController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Maze");
        primaryStage.setMaximized(true);
        primaryStage.setOnShown((w) -> {
            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(MainApp.class.getResource("/layouts/authorization.fxml"));
                AnchorPane rootLayout = loader.load();
                Scene scene = new Scene(rootLayout);
                AuthorizationController authorizationController = ((AuthorizationController)loader.getController());
                authorizationController.init(stage, scene);
                MazeObserver.addResponseAuthorizationEventHandler(authorizationController);
                logger.info("File (layouts/authorization.fxml) was loading");
                stage.setOnCloseRequest((event -> {
                    System.exit(0);
                }));
                stage.setTitle("Authorization");
                stage.setScene(scene);
                stage.initOwner(primaryStage);
                stage.initModality(Modality.WINDOW_MODAL);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setResizable(false);
        logger.info("Primary stage is initialized");
        initStage();
        primaryStage.show();
        logger.info("Primary stage is showing");
        rootLayoutController.init(primaryStage);
    }

    private void initStage() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/layouts/main.fxml"));
            AnchorPane rootLayout = loader.load();
            logger.info("File (layouts/main.fxml) was loading");
            rootLayoutController = loader.getController();
            MazeObserver.addMazeListener(rootLayoutController);
            MazeObserver.addGameListener(new GameListenerImpl<>());
            SendTurnPlayer sendTurnPlayer = new SendTurnPlayer();
            rootLayout.setOnKeyPressed(sendTurnPlayer);
            ConnectServerObserver.addSendTurnPlayer(sendTurnPlayer);
            WaitServerMessageTask waitServerMessageTask = new WaitServerMessageTask();
            ConnectServerObserver.addWaitServerMessageTask(waitServerMessageTask);
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            logger.info("Scene was installed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
