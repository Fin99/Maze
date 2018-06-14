package com.fin.controllers;

import com.fin.MainApp;
import com.fin.connects.database.ConnectDatabaseObserver;
import com.fin.connects.database.event.AuthorizationEvent;
import com.fin.connects.database.event.ResponseAuthorizationEvent;
import com.fin.connects.database.event.ResponseRegistrationEvent;
import com.fin.entity.User;
import com.fin.maze.MazeObserver;
import com.fin.maze.loginListener.ResponseAuthorizationEventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AuthorizationController implements Initializable, ResponseAuthorizationEventHandler {
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
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        exit.setOnAction(event -> {
            System.exit(0);
        });
        login.setDisable(true);
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            login.setDisable(newValue.trim().isEmpty() || password.getText().trim().isEmpty());
        });
        password.textProperty().addListener((observable, oldValue, newValue) -> {
            login.setDisable(newValue.trim().isEmpty() || username.getText().trim().isEmpty());
        });
        login.setOnAction(event -> {
            ConnectDatabaseObserver.processAuthorization(new AuthorizationEvent(new User(0, username.getText(), password.getText())));
        });
    }

    public void init(Stage stage, Scene scene1) {
        this.stage = stage;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/layouts/registration.fxml"));
            AnchorPane rootLayout = loader.load();
            Scene scene = new Scene(rootLayout);
            RegistrationController registrationController = ((RegistrationController) loader.getController());
            registrationController.init(stage, scene1);
            MazeObserver.addResponseRegistrationEventHandler(registrationController);
            logger.info("File (layouts/registration.fxml) was loading");
            registration.setOnAction(event -> {
                stage.setTitle("Registration");
                stage.setScene(scene);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(ResponseAuthorizationEvent responseAuthorizationEvent) {
        if(responseAuthorizationEvent.isResult()){
            stage.close();
        } else {
            stage.setTitle("Authorization: Incorrect data");
        }
    }
}
