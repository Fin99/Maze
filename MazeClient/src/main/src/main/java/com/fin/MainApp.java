package com.fin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainApp extends Application {

    static Stage primaryStage;
    private AnchorPane rootLayout;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Maze");
        this.primaryStage.setMaximized(true);
        this.primaryStage.initStyle(StageStyle.UNDECORATED);
        initRootLayout();
    }

    //инициализация макета
    private void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/main.fxml"));
            rootLayout = loader.load();
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            scene.setOnKeyPressed(new KeyListener());
            ExecutorService service = Executors.newFixedThreadPool(1);
            ServerMessageTask task = new ServerMessageTask();
            task.setOnSucceeded(new ServerMessageHandler(rootLayout));
            service.submit(task);

            Line divideLine = new Line(rootLayout.getHeight(), 0, rootLayout.getHeight(), rootLayout.getHeight());
            divideLine.setOpacity(1);
            divideLine.setId("1");
            rootLayout.getChildren().add(divideLine);
            ImageView bag = (ImageView) findElementByID("bag", rootLayout);
            bag.setFitHeight(rootLayout.getHeight() / 3);
            bag.setFitWidth(rootLayout.getHeight() / 3);
            bag.setX(rootLayout.getHeight() + (rootLayout.getWidth() - rootLayout.getHeight() - bag.getFitWidth()) / 1.8);
            bag.setY(rootLayout.getHeight() / 6 * 0.05);
            bag.setVisible(true);
            Button button = (Button) findElementByID("menu", rootLayout);
            button.setPrefWidth(rootLayout.getWidth() - rootLayout.getHeight());
            button.setPrefHeight(rootLayout.getHeight() / 6);
            button.setLayoutX(rootLayout.getHeight());
            button.setLayoutY(rootLayout.getHeight() - rootLayout.getHeight() / 6);
            button.setVisible(true);
            button.fire();
            Label infLabel = (Label) findElementByID("infLabel", rootLayout);
            infLabel.setPrefWidth(rootLayout.getWidth() - rootLayout.getHeight());
            infLabel.setPrefHeight(rootLayout.getHeight() / 6);
            infLabel.setLayoutX(rootLayout.getHeight());
            infLabel.setLayoutY(rootLayout.getHeight() - 2 * rootLayout.getHeight() / 6);
            infLabel.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Node findElementByID(String id, Pane parent) {
        for (Node element : parent.getChildren()) {
            if (element.getId() != null && element.getId().equals(id)) return element;
        }
        return null;
    }
}
