package com.fin.controllers;

import com.fin.connects.database.ConnectDatabaseObserver;
import com.fin.connects.database.event.RegistrationEvent;
import com.fin.connects.database.event.ResponseRegistrationEvent;
import com.fin.entity.User;
import com.fin.maze.loginListener.ResponseRegistrationEventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class RegistrationController implements Initializable, ResponseRegistrationEventHandler {
    //logger
    private final Logger logger = LogManager.getRootLogger();
    //
    @FXML
    TextField username;
    @FXML
    TextField password;
    @FXML
    Button registration;
    @FXML
    Button login;
    @FXML
    Button exit;
    @FXML
    AnchorPane root;

    private Stage stage;

    public RegistrationController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        exit.setOnAction(event -> System.exit(0));
        registration.setDisable(true);
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            registration.setDisable(newValue.trim().isEmpty() || password.getText().trim().isEmpty());
        });
        password.textProperty().addListener((observable, oldValue, newValue) -> {
            registration.setDisable(newValue.trim().isEmpty() || username.getText().trim().isEmpty());
        });
        registration.setOnAction(event -> {
            ConnectDatabaseObserver.processRegistration(new RegistrationEvent(new User(0, username.getText(), password.getText())));
        });
    }

    public void init(Stage stage, Scene scene) {
        this.stage = stage;
        login.setOnAction(event -> {
            stage.setTitle("Authorization");
            stage.setScene(scene);
        });
    }

    @Override
    public void handle(ResponseRegistrationEvent responseRegistrationEvent) {
        if(responseRegistrationEvent.isResult()){
            stage.close();
        } else {
            stage.setTitle("Registration: Select a different user name and password");
        }
    }
}
