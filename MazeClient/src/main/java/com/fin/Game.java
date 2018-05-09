package com.fin;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class Game implements Initializable {
    @FXML
    private GridPane cover;
    @FXML
    private Button menu;
    @FXML
    private ImageView human;
    @FXML
    private ImageView gun;
    @FXML
    private ImageView key;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}
