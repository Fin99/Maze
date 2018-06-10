package com.fin.controllers;

import com.fin.controllers.processButtonAction.MenuButtonActionListeners;
import com.fin.game.cover.Direction;
import com.fin.game.cover.Field;
import com.fin.game.player.Player;
import com.fin.maze.gameEvent.*;
import com.fin.maze.gameListeners.*;
import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class MazeController implements Initializable,
        InventoryListener, MazeListener, PlayersListener, MoveListener, ShotListener, ResizeListener {

    //logger
    private final Logger logger = LogManager.getRootLogger();
    //

    @FXML
    AnchorPane root;
    @FXML
    ImageView human;
    @FXML
    ImageView keyInMaze;
    @FXML
    ImageView monster;
    @FXML
    ImageView gunInMaze;
    @FXML
    ImageView key;
    @FXML
    ImageView gun;
    @FXML
    ImageView bag;
    @FXML
    ImageView bullet;
    @FXML
    Button menu;
    @FXML
    Label infLabel;

    private Map<Player, ImageView> anotherPlayers;
    private int ourPlayerID;
    private double coefficient;
    private Line divisionLine;

    {
        anotherPlayers = new HashMap<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void init(Stage root) {
        logger.info("Start MazeController.init()");
        menu.setOnAction(new MenuButtonActionListeners(root));
        logger.info("New listener for menu(Button) was installed");
        resizeImageView(root);
        pressMenu();
    }

    @Override
    public void handle(InventoryEvent inventoryEvent) {
        logger.info("Process InventoryEvent...");
        logger.info("GunInMaze = " + inventoryEvent.getGunInMaze()
                + ". KeyInMaze = " + inventoryEvent.getKeyInMaze() +
                ". Gun = " + inventoryEvent.getGun() +
                ". Key = " + inventoryEvent.getKey());
        gunInMaze.setVisible(inventoryEvent.getGunInMaze());
        keyInMaze.setVisible(inventoryEvent.getKeyInMaze());
        gun.setVisible(inventoryEvent.getGun());
        key.setVisible(inventoryEvent.getKey());
        logger.info("Properties visible changed. Process InventoryEvent is finished");
    }

    @Override
    public void handle(MazeEvent mazeEvent) {
        logger.info("Process MazeEvent...");
        //delete line
        root.getChildren().removeIf(node -> node instanceof Line && !node.equals(divisionLine));
        logger.info("All line on field deleted");
        //draw line
        for (Field field : mazeEvent.getCover().getCov()) {
            Line up = new Line(field.getX() * coefficient, field.getY() * coefficient, field.getX() * coefficient + coefficient, field.getY() * coefficient);
            Line down = new Line(field.getX() * coefficient, field.getY() * coefficient + coefficient, field.getX() * coefficient + coefficient, field.getY() * coefficient + coefficient);
            Line left = new Line(field.getX() * coefficient, field.getY() * coefficient, field.getX() * coefficient, field.getY() * coefficient + coefficient);
            Line right = new Line(field.getX() * coefficient + coefficient, field.getY() * coefficient, field.getX() * coefficient + coefficient, field.getY() * coefficient + coefficient);
            if (!field.containsWall(Direction.UP)) {
                up.setOpacity(.002);
                up.getStrokeDashArray().addAll(10., 5.);
            }
            if (!field.containsWall(Direction.DOWN)) {
                down.setOpacity(.2);
                down.getStrokeDashArray().addAll(10., 5.);
            }
            if (!field.containsWall(Direction.LEFT)) {
                left.setOpacity(.2);
                left.getStrokeDashArray().addAll(10., 5.);
            }
            if (!field.containsWall(Direction.RIGHT)) {
                right.setOpacity(.2);
                right.getStrokeDashArray().addAll(10., 5.);
            }
            root.getChildren().add(up);
            root.getChildren().add(down);
            root.getChildren().add(left);
            root.getChildren().add(right);
        }
        logger.info("Line redrawn. Process MazeEvent is finished");
    }

    @Override
    public void handle(PlayersEvent playersEvent) {
        logger.info("Process PlayersEvent...");
        ourPlayerID = playersEvent.getPlayers().get(0).getId();
        //delete all old players
        for (ImageView iv : anotherPlayers.values()) {
            if (iv == monster) {
                monster.setVisible(false);
            } else {
                root.getChildren().remove(iv);
            }
        }
        logger.info("All players removed from field");
        //add new player and part old player
        for (Player p : playersEvent.getPlayers()) {
            if (p.getId() == -1) {
                anotherPlayers.put(p, monster);
                monster.setVisible(true);
            } else if (p.getId() != ourPlayerID) {
                ImageView anotherPlayer = new ImageView(MazeController.class.getResource("/images/anotherPlayer.png").toString());
                anotherPlayer.setFitHeight(coefficient);
                anotherPlayer.setFitWidth(coefficient);
                anotherPlayer.setX(p.getX() * coefficient);
                anotherPlayer.setY(p.getY() * coefficient);
                anotherPlayers.put(p, anotherPlayer);
            }
        }
        logger.info("Players redrawn. Process PlayersEvent is finished");
    }

    @Override
    public void handle(MoveEvent moveEvent) {
        logger.info("Process MoveEvent...");
        ImageView player;
        if (ourPlayerID == moveEvent.getPlayer().getId()) {
            player = human;
        } else {
            player = anotherPlayers.get(moveEvent.getPlayer());
        }
        logger.info("Type move is: " + moveEvent.getType());
        if (moveEvent.getType().equals("Fade")) {
            FadeTransition fadeTransition = new FadeTransition();
            fadeTransition.setNode(player);
            fadeTransition.setFromValue(1.);
            fadeTransition.setToValue(0.);
            fadeTransition.setDuration(Duration.seconds(1));
            logger.info("Start fade animation");
            fadeTransition.play();
            fadeTransition.setOnFinished((w) -> {
                logger.info("1 part animation(faded) is finished");
                player.setX(moveEvent.getFinish().getX() * coefficient);
                player.setY(moveEvent.getFinish().getY() * coefficient);
                FadeTransition fadeTransition2 = new FadeTransition();
                fadeTransition2.setNode(player);
                fadeTransition2.setFromValue(0.);
                fadeTransition2.setToValue(1.);
                fadeTransition2.setDuration(Duration.seconds(1));
                logger.info("Start 2 part animation(faded)...");
                fadeTransition2.play();
                fadeTransition2.setOnFinished((w2) -> {
                    logger.info("2 part animation(faded) is finished");
                });
            });
        }
        if (moveEvent.getType().equals("Move")) {
            Line path = new Line(moveEvent.getStart().getX() * coefficient + coefficient * 0.5,
                    moveEvent.getStart().getY() * coefficient + coefficient * 0.5,
                    moveEvent.getFinish().getX() * coefficient + coefficient * 0.5,
                    moveEvent.getFinish().getY() * coefficient + coefficient * 0.5);
            PathTransition pathTransition = new PathTransition(Duration.seconds(0.5), path, player);
            logger.info("Start move animation");
            pathTransition.play();
            pathTransition.setOnFinished((w) -> {
                logger.info("Animation(move) is finished");
                player.setX(moveEvent.getFinish().getX() * coefficient);
                player.setY(moveEvent.getFinish().getY() * coefficient);
            });
        }
        logger.info("Process MoveEvent is finished");
    }

    @Override
    public void handle(ResizeEvent resizeEvent) {
        logger.info("Process ResizeEvent...");

        coefficient = root.getHeight() / resizeEvent.getSize();
        logger.info("New coefficient initialized. Coefficient = " + coefficient);

        human.setFitHeight(coefficient);
        human.setFitWidth(coefficient);
        human.setVisible(true);

        gunInMaze.setFitHeight(coefficient);
        gunInMaze.setFitWidth(coefficient);

        keyInMaze.setFitHeight(coefficient);
        keyInMaze.setFitWidth(coefficient);

        monster.setFitHeight(coefficient);
        monster.setFitWidth(coefficient);

        gun.setFitHeight(root.getHeight() / 6);
        gun.setFitWidth(root.getHeight() / 6);
        gun.setX(root.getHeight() + ((root.getWidth() - root.getHeight()) / 2 - gun.getFitWidth()) / 2);
        gun.setY(root.getHeight() / 6 * 2.2);

        key.setFitHeight(root.getHeight() / 6);
        key.setFitWidth(root.getHeight() / 6);
        key.setX(root.getHeight() + (root.getWidth() - root.getHeight()) / 2 + ((root.getWidth() - root.getHeight()) / 2 - key.getFitWidth()) / 2);
        key.setY(root.getHeight() / 3);

        bullet.setFitWidth(coefficient);
        bullet.setFitHeight(coefficient);

        logger.info("All images resized. Process ReesizeEvent is finished");
    }

    @Override
    public void handle(ShotEvent shotEvent) {
        logger.info("Process ShotEvent...");
        bullet.setVisible(true);
        Line path = null;
        switch (shotEvent.getDirection()) {
            case UP:
                logger.info("Bullet will fly up");
                bullet.setRotate(-90);
                path = new Line(shotEvent.getStart().getX() * coefficient + coefficient * 0.5,
                        shotEvent.getStart().getY() * coefficient - coefficient * 0.25,
                        shotEvent.getFinish().getX() * coefficient + coefficient * 0.5,
                        shotEvent.getFinish().getY() * coefficient + coefficient * 0.5);
                break;
            case DOWN:
                logger.info("Bullet will fly down");
                bullet.setRotate(90);
                path = new Line(shotEvent.getStart().getX() * coefficient + coefficient * 0.5,
                        shotEvent.getStart().getY() * coefficient + coefficient * 1.25,
                        shotEvent.getFinish().getX() * coefficient + coefficient * 0.5,
                        shotEvent.getFinish().getY() * coefficient + coefficient * 0.5);
                break;
            case LEFT:
                bullet.setRotate(180);
                path = new Line(shotEvent.getStart().getX() * coefficient,
                        shotEvent.getStart().getY() * coefficient + coefficient * 0.5,
                        shotEvent.getFinish().getX() * coefficient + coefficient * 0.5,
                        shotEvent.getFinish().getY() * coefficient + coefficient * 0.5);
                break;
            case RIGHT:
                logger.info("Bullet will fly right");
                path = new Line(shotEvent.getStart().getX() * coefficient + coefficient,
                        shotEvent.getStart().getY() * coefficient + coefficient * 0.5,
                        shotEvent.getFinish().getX() * coefficient + coefficient * 0.5,
                        shotEvent.getFinish().getY() * coefficient + coefficient * 0.5);
                break;
        }
        PathTransition pathTransition = new PathTransition(Duration.seconds(1), path, bullet);
        logger.info("Animation(shot) is started");
        pathTransition.play();
        pathTransition.setOnFinished((w) -> {
            logger.info("Animation(shot) is finished");
            bullet.setVisible(false);
        });
        logger.info("Process ShotEvent is finished.");
    }

    public void pressMenu() {
        menu.fire();
    }

    public void resizeImageView(Stage primaryStage) {
        logger.info("Start MazeController.resizeImageView()");

        root.setPrefWidth(primaryStage.getWidth());
        root.setPrefHeight(primaryStage.getHeight());
        divisionLine = new Line(root.getHeight(), 0, root.getHeight(), root.getHeight());
        divisionLine.setOpacity(1);
        root.getChildren().add(divisionLine);

        bag.setFitHeight(root.getHeight() / 3);
        bag.setFitWidth(root.getHeight() / 3);
        bag.setLayoutX(root.getHeight() + (root.getWidth() - root.getHeight() - bag.getFitWidth()) / 1.8);
        bag.setLayoutY(root.getHeight() / 6 * 0.05);
        bag.setVisible(true);

        menu.setPrefWidth(root.getWidth() - root.getHeight());
        menu.setPrefHeight(root.getHeight() / 6);
        menu.setLayoutX(root.getHeight());
        menu.setLayoutY(root.getHeight() - root.getHeight() / 6);
        menu.setVisible(true);

        infLabel.setPrefWidth(root.getWidth() - root.getHeight());
        infLabel.setPrefHeight(root.getHeight() / 6);
        infLabel.setLayoutX(root.getHeight());
        infLabel.setLayoutY(root.getHeight() - 2 * root.getHeight() / 6);
        infLabel.setVisible(true);

        logger.info("All images resized.");
    }
}
