package com.fin;

import com.fin.game.cover.Direction;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class MainApp extends Application {

    private Stage primaryStage;
    private AnchorPane rootLayout;
    private Connect server;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Maze");
        connectToServer();
        initRootLayout();
    }

    private void connectToServer() {
        try {
            server = new Connect(new Socket(InetAddress.getLocalHost(), 2600));
        } catch (IOException e) {
            System.err.println("Ошибка при подключении к серверу");
            System.exit(1);
        }

    }

    //инициализация макета
    private void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/main.fxml"));
            rootLayout = loader.load();

            Scene scene = new Scene(rootLayout);
            scene.setOnKeyPressed(new KeyListener(rootLayout, server));
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
