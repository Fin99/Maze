package com.fin.controllers;

import com.fin.Windows1251Control;
import com.fin.connects.database.ConnectDatabaseObserver;
import com.fin.connects.database.event.RegistrationEvent;
import com.fin.connects.database.event.ResponseRegistrationEvent;
import com.fin.entity.User;
import com.fin.maze.MazeObserver;
import com.fin.maze.localEvent.LocalEvent;
import com.fin.maze.localHandlers.LocalHandler;
import com.fin.maze.loginListener.ResponseRegistrationEventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class RegistrationController implements Initializable, ResponseRegistrationEventHandler, LocalHandler {
    //logger
    private final Logger logger = LogManager.getRootLogger();
    //
    @FXML
    Label usernameLabel;
    @FXML
    Label passwordLabel;
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
    @FXML
    Button local;

    private Stage stage;
    private ResourceBundle resourceBundle;

    {
        MazeObserver.addLocaleHandler(this);
        resourceBundle = ResourceBundle.getBundle("strings", new Windows1251Control());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usernameLabel.setText(resourceBundle.getString("username"));
        username.setPromptText(resourceBundle.getString("username"));
        password.setPromptText(resourceBundle.getString("password"));
        passwordLabel.setText(resourceBundle.getString("password"));
        registration.setText(resourceBundle.getString("registration"));
        login.setText(resourceBundle.getString("authorization"));
        exit.setText(resourceBundle.getString("exit"));
        exit.setOnAction(event -> System.exit(0));
        local.setText(resourceBundle.getString("local"));
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
        local.setOnAction(event -> {
            MazeObserver.processLocaleEvent(new LocalEvent(local.getText().equals("en")?Locale.US:new Locale("ru_RU")));
        });
    }

    public void init(Stage stage, Scene scene) {
        this.stage = stage;
        login.setOnAction(event -> {
            stage.setTitle(resourceBundle.getString("authorization"));
            stage.setScene(scene);
        });
    }

    @Override
    public void handle(ResponseRegistrationEvent responseRegistrationEvent) {
        if (responseRegistrationEvent.isResult()) {
            stage.close();
        } else {
            stage.setTitle(resourceBundle.getString("error_registration"));
        }
    }

    @Override
    public void handle(LocalEvent localEvent) {
        boolean stageTitleRegistration = stage.getTitle().equals(resourceBundle.getString("registration"));
        boolean stageTitleErrorRegistration = stage.getTitle().equals(resourceBundle.getString("error_registration"));
        resourceBundle = ResourceBundle.getBundle("strings", localEvent.getLocale(), new Windows1251Control());
        usernameLabel.setText(resourceBundle.getString("username"));
        username.setPromptText(resourceBundle.getString("username"));
        password.setPromptText(resourceBundle.getString("password"));
        passwordLabel.setText(resourceBundle.getString("password"));
        registration.setText(resourceBundle.getString("registration"));
        login.setText(resourceBundle.getString("authorization"));
        exit.setText(resourceBundle.getString("exit"));
        local.setText(resourceBundle.getString("local"));
        if(stageTitleRegistration){
            stage.setTitle(resourceBundle.getString("registration"));
        } else if(stageTitleErrorRegistration){
            stage.setTitle(resourceBundle.getString("error_registration"));
        }
    }
}
