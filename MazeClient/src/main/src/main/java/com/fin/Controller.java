package com.fin;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller implements Initializable {
    @FXML
    AnchorPane root;
    @FXML
    Button menu;
    @FXML
    Label infLabel;

    private Scene dialogScene;
    private Scene searchScene;
    private Stage dialogStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menu.setOnAction(new EventHandler<ActionEvent>() {
            boolean isFirst = true;
            @Override
            public void handle(ActionEvent event) {
                if (isFirst) {
                    initDialogStage();
                    loadDialogScene();
                    loadSearchScene();
                    isFirst = false;
                }
                dialogStage.setScene(dialogScene);
                dialogStage.showAndWait();
            }
        });
    }

    private void initDialogStage() {
        dialogStage = new Stage();
        dialogStage.setTitle("Menu");
        dialogStage.setScene(dialogScene);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(MainApp.primaryStage);
        dialogStage.setResizable(false);
        dialogStage.initStyle(StageStyle.DECORATED);
    }

    private void loadSearchScene() {
        AnchorPane searchServer = null;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Controller.class.getResource("/searchServer.fxml"));
            searchServer = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        searchScene = new Scene(searchServer);
        searchServer.setPrefWidth(root.getWidth() / 3);
        searchServer.setPrefHeight(root.getHeight() / 3);
        Label label = (Label) findElementByID("label", searchServer);
        label.setPrefHeight(searchServer.getPrefHeight() / 3);
        label.setPrefWidth(searchServer.getPrefWidth());
        label.setLayoutX(0);
        label.setLayoutY(0);
        TextField textField = (TextField) findElementByID("ipServer", searchServer);
        textField.setPrefWidth(searchServer.getPrefWidth());
        textField.setPrefHeight(searchServer.getPrefHeight() / 3);
        textField.setLayoutX(0);
        textField.setLayoutY(searchServer.getPrefHeight() / 3);
        Button search = (Button) findElementByID("search", searchServer);
        search.setPrefWidth(searchServer.getPrefWidth());
        search.setPrefHeight(searchServer.getPrefHeight() / 3);
        search.setLayoutX(0);
        search.setLayoutY(searchServer.getPrefHeight() / 3 * 2);
        search.setOnAction((event14) -> {
            String ip = textField.getText();
            try {
                if (ip != null && !ip.equals("")) {
                    String[] ipAndPort = ip.split(":");
                    Socket socket = new Socket(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
                    Connect.setServer(socket);
                    ExecutorService service = Executors.newFixedThreadPool(1);
                    ServerMessageTask task = new ServerMessageTask();
                    task.setOnSucceeded(new ServerMessageHandler(root));
                    service.submit(task);
                    Connect.sendRequest("Move", null);
                    dialogStage.close();
                }
            }catch (IOException ioe){
                label.setText("Введите адрес сервера повторно");
            }
        });
    }

    private void loadDialogScene() {
        AnchorPane dialogPane = null;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Controller.class.getResource("/menu.fxml"));
            dialogPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dialogScene = new Scene(dialogPane);
        //
        dialogPane.setPrefHeight(root.getHeight() / 3);
        dialogPane.setPrefWidth(root.getWidth() / 3);
        Button restartGame = (Button) findElementByID("restartGame", dialogPane);
        restartGame.setPrefHeight(dialogPane.getPrefHeight() / 3);
        restartGame.setPrefWidth(dialogPane.getPrefWidth());
        restartGame.setLayoutX(0);
        restartGame.setLayoutY(0);
        restartGame.setOnAction(event12 -> Connect.sendRequest("Update maze", null));
        Button exit = (Button) findElementByID("exit", dialogPane);
        exit.setPrefHeight(dialogPane.getPrefHeight() / 3);
        exit.setPrefWidth(dialogPane.getPrefWidth());
        exit.setLayoutX(0);
        exit.setLayoutY(dialogPane.getPrefHeight() / 3 * 2);
        exit.setOnAction(event1 -> System.exit(0));
        Button reconnectServer = (Button) findElementByID("reconnectServer", dialogPane);
        reconnectServer.setPrefHeight(dialogPane.getPrefHeight() / 3);
        reconnectServer.setPrefWidth(dialogPane.getPrefWidth());
        reconnectServer.setLayoutX(0);
        reconnectServer.setLayoutY(dialogPane.getPrefHeight() / 3);
        reconnectServer.setOnAction(event13 -> {
            dialogStage.setScene(searchScene);
        });
    }

    private Node findElementByID(String id, Pane parent) {
        for (Node element : parent.getChildren()) {
            if (element.getId() != null && element.getId().equals(id)) return element;
        }
        return null;
    }
}
