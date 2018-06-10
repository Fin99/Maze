package com.fin;

import com.fin.connects.ConnectObserver;
import com.fin.connects.SendTurnPlayer;
import com.fin.connects.WaitServerMessageTask;
import com.fin.controllers.MazeController;
import com.fin.maze.GameListenerImpl;
import com.fin.maze.MazeObserver;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class MainApp extends Application {
    //logger
    private final Logger logger = LogManager.getRootLogger();
    //
    private Stage primaryStage;
    private MazeController rootLayoutController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("MazeObserver");
        primaryStage.setMaximized(true);
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
            ConnectObserver.addSendTurnPlayer(sendTurnPlayer);
            WaitServerMessageTask waitServerMessageTask = new WaitServerMessageTask();
            ConnectObserver.addWaitServerMessageTask(waitServerMessageTask);
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            logger.info("Scene was installed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
