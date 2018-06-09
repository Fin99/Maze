package com.fin.controllers.processButtonAction;

import com.fin.controllers.SearchController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class ReconnectServerButtonActionListeners implements EventHandler<ActionEvent> {
    //logger
    private final Logger logger = LogManager.getRootLogger();
    //
    private Scene searchScene;
    private Stage root;

    public ReconnectServerButtonActionListeners(AnchorPane menu, Stage menuStage) {
        logger.info("Create new ReconnectServerButtonActionListener");
        this.root = menuStage;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ReconnectServerButtonActionListeners.class.
                    getResource("/layouts/searchServer.fxml"));
            searchScene = new Scene(loader.load());
            logger.info("Layout(/layouts/searchServer.fxml) loaded");
            ((SearchController) loader.getController()).setMenuStage(menu, menuStage);
        } catch (IOException e) {
            logger.fatal("Exception sprung at boot time layout(/layouts/searchServer.fxml)");
            e.printStackTrace();
        }
    }

    @Override
    public void handle(ActionEvent event) {
        logger.info("ReconnectServer(button) pressed");
        root.setScene(searchScene);
    }
}
