package com.fin.controllers;

import com.fin.connects.Connect;
import com.fin.connects.ConnectObserver;
import com.fin.connects.event.ReplacementConnectEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class SearchController implements Initializable {
    //logger
    private final Logger logger = LogManager.getRootLogger();
    //
    @FXML
    AnchorPane root;
    @FXML
    Label label;
    @FXML
    TextField ipServer;
    @FXML
    Button search;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setMenuStage(AnchorPane menu, Stage menuStage) {
        logger.info("Start setMenuStage()");
        root.setPrefWidth(menu.getPrefWidth());
        root.setPrefHeight(menu.getPrefHeight());
        logger.info("New size for SearchStage was installed");

        label.setPrefHeight(root.getPrefHeight() / 3);
        label.setPrefWidth(root.getPrefWidth());
        label.setLayoutX(0);
        label.setLayoutY(0);
        logger.info("New size for infoLabel was installed");

        ipServer.setPrefWidth(root.getPrefWidth());
        ipServer.setPrefHeight(root.getPrefHeight() / 3);
        ipServer.setLayoutX(0);
        ipServer.setLayoutY(root.getPrefHeight() / 3);
        logger.info("New size for ipServer(TextField) was installed");

        search.setPrefWidth(root.getPrefWidth());
        search.setPrefHeight(root.getPrefHeight() / 3);
        search.setLayoutX(0);
        search.setLayoutY(root.getPrefHeight() / 3 * 2);
        logger.info("New size for search(Button) was installed");
        search.setOnAction((ae) -> {
            logger.info("Process SearchAction..");
            String ip = ipServer.getText();
            logger.info("IP(String) was initialized : " + ip);
            try {
                if (ip != null && !ip.equals("")) {
                    String[] ipAndPort = ip.split(":");
                    Socket socket = new Socket(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
                    logger.info("Socket("+ip+") was initialized");
                    logger.info("ReplacementConnectEvent was creating");
                    ConnectObserver.processReplacementConnectEvent(new ReplacementConnectEvent(new Connect(socket)));
                    menuStage.close();
                    logger.info("MenuStage is closed");
                }
            } catch (IOException ioe) {
                logger.error("IP is invalid or another IOE exception");
                label.setText("Введите адрес сервера повторно");
            }
            logger.info("Process SearchAction is finished");
        });
        logger.info("New listener for search(Button) was installed");
    }
}
