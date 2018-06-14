package com.fin.controllers.processButtonAction;

import com.fin.Windows1251Control;
import com.fin.controllers.MenuController;
import com.fin.maze.MazeObserver;
import com.fin.maze.localEvent.LocalEvent;
import com.fin.maze.localHandlers.LocalHandler;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ResourceBundle;

public class MenuButtonActionListeners implements EventHandler<ActionEvent>, LocalHandler {
    //logger
    private final Logger logger = LogManager.getRootLogger();
    //
    private Stage menuStage;
    private Scene menu;
    private ResourceBundle resourceBundle;

    {
        MazeObserver.addLocaleHandler(this);
        resourceBundle = ResourceBundle.getBundle("strings", new Windows1251Control());
    }

    public MenuButtonActionListeners(Stage rootStage) {
        logger.info("Create new MenuButtonActionListener");
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ReconnectServerButtonActionListeners.class.
                    getResource("/layouts/menu.fxml"));
            AnchorPane menuPane = loader.load();
            logger.info("Layout(/layouts/menu.fxml) loaded");
            menuPane.setPrefWidth(rootStage.getWidth() / 3);
            menuPane.setPrefHeight(rootStage.getHeight() / 3);
            menu = new Scene(menuPane);

            menuStage = new Stage();
            menuStage.setOnCloseRequest((we) -> {
                logger.info("Menu closed.");
                menuStage.setScene(menu);
                menuStage.close();
            });
            menuStage.setTitle(resourceBundle.getString("menu"));
            menuStage.setScene(menu);
            menuStage.initModality(Modality.WINDOW_MODAL);
            menuStage.initOwner(rootStage);
            menuStage.setResizable(false);
            menuStage.initStyle(StageStyle.DECORATED);
            ((MenuController) loader.getController()).init(menuStage);
        } catch (IOException e) {
            logger.fatal("Exception sprung at boot time layout(/layouts/menu.fxml)");
            e.printStackTrace();
        }
    }

    @Override
    public void handle(ActionEvent event) {
        logger.info("Menu(button) pressed");
        menuStage.setScene(menu);
        menuStage.showAndWait();
    }

    @Override
    public void handle(LocalEvent event) {
        resourceBundle = ResourceBundle.getBundle("strings", event.getLocale(), new Windows1251Control());
        menuStage.setTitle(resourceBundle.getString("menu"));
    }
}
