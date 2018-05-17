package com.fin;

import com.fin.game.cover.Direction;
import com.fin.game.cover.Field;
import com.fin.game.maze.Maze;
import com.fin.game.player.Item;
import com.fin.game.player.Position;
import javafx.animation.PathTransition;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMessageHandler implements EventHandler<WorkerStateEvent> {
    static volatile boolean messageFlag;

    private boolean isFirstCall;

    private Connect server;

    private AnchorPane rootLayout;
    private double coefficient;
    private Maze maze;
    private ImageView human;
    private ImageView monster;
    private ImageView keyInMaze;
    private ImageView gunInMaze;
    private ImageView key;
    private ImageView gun;
    private List<ImageView> anotherPlayers;

    {
        anotherPlayers = new ArrayList<>();
        isFirstCall = true;
    }

    public ServerMessageHandler(AnchorPane rootLayout, Connect server) {
        this.rootLayout = rootLayout;
        this.server = server;
    }

    @Override
    public void handle(WorkerStateEvent event) {
        if (isFirstCall) {
            firstCall((ServerMessage) event.getSource().getValue());
            isFirstCall = false;
        } else {
            simpleCall((ServerMessage) event.getSource().getValue());
        }
        ExecutorService service = Executors.newFixedThreadPool(1);
        ServerMessageTask task = new ServerMessageTask(server);
        task.setOnSucceeded(this);
        service.submit(task);
    }

    private void simpleCall(ServerMessage message) {
        maze = message.getMaze();
        messageFlag = message.amIGoingNow();
        //delete old positions another players
        for (ImageView image : anotherPlayers) {
            rootLayout.getChildren().remove(image);
        }
        anotherPlayers.clear();
        //change my position
        if (human.getX() != maze.getFirstPlayer().getX() * coefficient || human.getY() != maze.getFirstPlayer().getY() * coefficient) {
            Line path = new Line(human.getX() + coefficient * 0.5, human.getY() + coefficient * 0.5, maze.getFirstPlayer().getX() * coefficient + coefficient * 0.5, maze.getFirstPlayer().getY() * coefficient + coefficient * 0.5);
            PathTransition pathTransition = new PathTransition(Duration.seconds(1), path, human);
            pathTransition.play();
        }
        human.setX(maze.getFirstPlayer().getX() * coefficient);
        human.setY(maze.getFirstPlayer().getY() * coefficient);
        //update cover maze
        for (Field field : maze.getCover().getCov()) {
            if (field.containsWall(Direction.UP)) {
                rootLayout.getChildren().add(new Line(field.getX() * coefficient, field.getY() * coefficient, field.getX() * coefficient + coefficient, field.getY() * coefficient));
            }
            if (field.containsWall(Direction.DOWN)) {
                rootLayout.getChildren().add(new Line(field.getX() * coefficient, field.getY() * coefficient + coefficient, field.getX() * coefficient + coefficient, field.getY() * coefficient + coefficient));
            }
            if (field.containsWall(Direction.LEFT)) {
                rootLayout.getChildren().add(new Line(field.getX() * coefficient, field.getY() * coefficient, field.getX() * coefficient, field.getY() * coefficient + coefficient));
            }
            if (field.containsWall(Direction.RIGHT)) {
                rootLayout.getChildren().add(new Line(field.getX() * coefficient + coefficient, field.getY() * coefficient, field.getX() * coefficient + coefficient, field.getY() * coefficient + coefficient));
            }
        }
        //update icon in bag
        if (maze.getFirstPlayer().contains("Key")) {
            key.setVisible(true);

        } else {
            key.setVisible(false);
        }
        if (maze.getFirstPlayer().contains("Gun")) {
            gun.setVisible(true);
        } else {
            gun.setVisible(false);
        }
        //update icon in cover
        Item keyMaze = null;
        for (Item i : maze.getItems()) {
            if (i.getName().equals("Key")) keyMaze = i;
        }
        updateImageInMaze(keyMaze, keyInMaze);
        Item gunMaze = null;
        for (Item i : maze.getItems()) {
            if (i.getName().equals("Gun")) gunMaze = i;
        }
        updateImageInMaze(gunMaze, gunInMaze);
        //add on cover icon players and monster
        for (int i = 1; i < maze.getPlayers().size(); i++) {
            if (maze.getPlayers().get(i).getId() == -1) {
                updateImageInMaze(maze.getPlayers().get(i), monster);
            } else {
                ImageView imageView = new ImageView(KeyListener.class.getResource("/anotherPlayer.png").toString());
                imageView.setFitHeight(coefficient);
                imageView.setFitWidth(coefficient);
                updateImageInMaze(maze.getPlayers().get(i), imageView);
                anotherPlayers.add(imageView);
                rootLayout.getChildren().add(imageView);
            }
        }
    }

    private void firstCall(ServerMessage message) {
        maze = message.getMaze();
        messageFlag = message.amIGoingNow();
        coefficient = rootLayout.getHeight() / maze.getSize();
        rootLayout.getChildren().add(new Line(rootLayout.getHeight(), 0, rootLayout.getHeight(), rootLayout.getHeight()));
        rootLayout.getChildren().add(new Line(rootLayout.getHeight(), 0, rootLayout.getHeight(), rootLayout.getHeight()));
        human = (ImageView) findElementByID("human", rootLayout);
        setDefaultSizeImage(human);
        human.setVisible(true);
        gunInMaze = (ImageView) findElementByID("gunInMaze", rootLayout);
        setDefaultSizeImage(gunInMaze);
        keyInMaze = (ImageView) findElementByID("keyInMaze", rootLayout);
        setDefaultSizeImage(keyInMaze);
        monster = (ImageView) findElementByID("monster", rootLayout);
        setDefaultSizeImage(monster);
        gun = (ImageView) findElementByID("gun", rootLayout);
        gun.setFitHeight(rootLayout.getHeight()/6);
        gun.setFitWidth(rootLayout.getHeight()/6);
        gun.setX(rootLayout.getHeight() + ((rootLayout.getWidth() - rootLayout.getHeight()) / 2 - gun.getFitWidth()) / 2);
        gun.setY(rootLayout.getHeight()/6 * 2.2);
        key = (ImageView) findElementByID("key", rootLayout);
        key.setFitHeight(rootLayout.getHeight()/6);
        key.setFitWidth(rootLayout.getHeight()/6);
        key.setX(rootLayout.getHeight() + (rootLayout.getWidth() - rootLayout.getHeight()) / 2 + ((rootLayout.getWidth() - rootLayout.getHeight()) / 2 - key.getFitWidth()) / 2);
        key.setY(rootLayout.getHeight()/3);
        ImageView bag = (ImageView) findElementByID("bag", rootLayout);
        bag.setFitHeight(rootLayout.getHeight()/3);
        bag.setFitWidth(rootLayout.getHeight()/3);
        bag.setX(rootLayout.getHeight() + (rootLayout.getWidth() - rootLayout.getHeight() - bag.getFitWidth()) / 1.8);
        bag.setY(rootLayout.getHeight()/6 * 0.05);
        bag.setVisible(true);
        Button button = (Button) findElementByID("menu", rootLayout);
        button.setPrefWidth(rootLayout.getWidth() - rootLayout.getHeight());
        button.setPrefHeight(rootLayout.getHeight()/6);
        button.setLayoutX(rootLayout.getHeight());
        button.setLayoutY(rootLayout.getHeight()-rootLayout.getHeight()/6);
        button.setVisible(true);
        TextArea infTextArea = (TextArea) findElementByID("infTextArea", rootLayout);
        infTextArea.setPrefWidth(rootLayout.getWidth() - rootLayout.getHeight());
        infTextArea.setPrefHeight(rootLayout.getHeight()/6);
        infTextArea.setLayoutX(rootLayout.getHeight());
        infTextArea.setLayoutY(rootLayout.getHeight()-2*rootLayout.getHeight()/6);
        infTextArea.setVisible(true);
    }

    private Node findElementByID(String id, Pane parent) {
        for (Node element : parent.getChildren()) {
            if (element.getId() != null && element.getId().equals(id)) return element;
        }
        return null;
    }

    private void updateImageInMaze(Position item, ImageView itemInMaze) {
        if (item != null) {
            itemInMaze.setX(item.getX() * coefficient);
            itemInMaze.setY(item.getY() * coefficient);
            itemInMaze.setVisible(true);
        } else {
            itemInMaze.setVisible(false);
        }
    }

    private void setDefaultSizeImage(ImageView image) {
        image.setFitWidth(coefficient);
        image.setFitHeight(coefficient);
    }
}
