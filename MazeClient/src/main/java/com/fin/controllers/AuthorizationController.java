package com.fin.controllers;

import com.fin.MainApp;
import com.fin.Windows1251Control;
import com.fin.connects.database.ConnectDatabaseObserver;
import com.fin.connects.database.event.AuthorizationEvent;
import com.fin.connects.database.event.ResponseAuthorizationEvent;
import com.fin.entity.User;
import com.fin.maze.MazeObserver;
import com.fin.maze.localEvent.LocalEvent;
import com.fin.maze.localHandlers.LocalHandler;
import com.fin.maze.loginListener.ResponseAuthorizationEventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class AuthorizationController implements Initializable, ResponseAuthorizationEventHandler, LocalHandler {
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
        passwordLabel.setText(resourceBundle.getString("password"));
        username.setPromptText(resourceBundle.getString("username"));
        password.setPromptText(resourceBundle.getString("password"));
        login.setText(resourceBundle.getString("authorization"));
        registration.setText(resourceBundle.getString("registration"));
        exit.setText(resourceBundle.getString("exit"));
        local.setText(resourceBundle.getString("local"));
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
        local.setOnAction(event -> {
            MazeObserver.processLocaleEvent(new LocalEvent(local.getText().equals("en")?Locale.US:new Locale("ru_RU")));
        });
    }

    public void init(Stage stage, Scene scene1) {
        this.stage = stage;
        stage.setTitle(resourceBundle.getString("authorization"));
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
                stage.setTitle(resourceBundle.getString("registration"));
                stage.setScene(scene);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(ResponseAuthorizationEvent responseAuthorizationEvent) {
        if (responseAuthorizationEvent.isResult()) {
            stage.close();
        } else {
            stage.setTitle(resourceBundle.getString("error_authorization"));
        }
    }

    @Override
    public void handle(LocalEvent localEvent) {
        boolean stageTitleAuthorization = stage.getTitle().equals(resourceBundle.getString("authorization"));
        boolean stageTitleErrorAuthorization = stage.getTitle().equals(resourceBundle.getString("error_authorization"));
        resourceBundle = ResourceBundle.getBundle("strings", localEvent.getLocale(), new Windows1251Control());
        usernameLabel.setText(resourceBundle.getString("username"));
        passwordLabel.setText(resourceBundle.getString("password"));
        username.setPromptText(resourceBundle.getString("username"));
        password.setPromptText(resourceBundle.getString("password"));
        login.setText(resourceBundle.getString("authorization"));
        registration.setText(resourceBundle.getString("registration"));
        exit.setText(resourceBundle.getString("exit"));
        local.setText(resourceBundle.getString("local"));
        stage.setTitle(resourceBundle.getString("authorization"));
        if(stageTitleAuthorization){
            stage.setTitle(resourceBundle.getString("authorization"));
        } else if(stageTitleErrorAuthorization){
            stage.setTitle(resourceBundle.getString("error_authorization"));
        }
    }
}
