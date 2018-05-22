package com.fin;

import com.fin.game.cover.Direction;
import com.fin.game.cover.Field;
import com.fin.game.maze.Maze;
import com.fin.game.player.Item;
import com.fin.game.player.Position;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
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
    static volatile Timeline timer;

    private boolean isFirstCall;

    private AnchorPane rootLayout;
    private double coefficient;
    private Maze maze;
    private ImageView human;
    private ImageView monster;
    private ImageView keyInMaze;
    private ImageView gunInMaze;
    private ImageView key;
    private ImageView gun;
    private Label label;

    volatile static int[] time = {10};
    private List<ImageView> anotherPlayers;

    {
        anotherPlayers = new ArrayList<>();
        isFirstCall = true;
    }

    public ServerMessageHandler(AnchorPane rootLayout) {
        this.rootLayout = rootLayout;
    }

    @Override
    public void handle(WorkerStateEvent event) {
        //check all null beside boolean and show alert
        if (isFirstCall) {
            firstCall((ServerMessage) event.getSource().getValue());
            isFirstCall = false;
        } else {
            ServerMessage message = (ServerMessage) event.getSource().getValue();
            if (message.getMaze() == null && message.amIGoingNow() != null) {
                if (message.amIGoingNow()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setTitle("Победа!");
                    alert.setContentText("Вы победили!");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setTitle("Поражение");
                    alert.setContentText("Вы проиграли...");
                    alert.showAndWait();
                }
            } else if (message.getMaze() != null && message.amIGoingNow() != null) {
                if (message.getBulletStart() != null && message.getBulletFinish() != null) {
                    shotCall(message);
                }
                try {
                    simpleCall(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (messageFlag) {
            if (timer != null) timer.pause();
            timer = new Timeline(new KeyFrame(Duration.seconds(1), event1 -> {
                if (messageFlag) {
                    label.setText("У вас осталось: " + time[0]--);
                    if (time[0] == -1)
                        label.setText("Время вышло");
                    if (time[0] == -1) {
                        time[0] = 10;
                        //Connect.sendRequest("Move", null);
                        //messageFlag = false;
                    }
                }
            }));
            timer.setCycleCount(11);
            timer.playFromStart();
        }
        ExecutorService service = Executors.newFixedThreadPool(1);
        ServerMessageTask task = new ServerMessageTask();
        task.setOnSucceeded(this);
        service.submit(task);
    }

    private void shotCall(ServerMessage message) {
        if (message.getBulletStart().getX() - message.getBulletFinish().getX() != 0 || message.getBulletStart().getY() - message.getBulletFinish().getY() != 0) {
            ImageView bullet = new ImageView(ServerMessageHandler.class.getResource("/bullet.png").toString());
            rootLayout.getChildren().add(bullet);
            setDefaultSizeImage(bullet);
            bullet.setVisible(true);
            Line path = null;
            if (message.getBulletStart().getX() - message.getBulletFinish().getX() > 0) {//left shot
                bullet.setRotate(180);
                path = new Line(message.getBulletStart().getX() * coefficient, message.getBulletStart().getY() * coefficient + coefficient * 0.5, message.getBulletFinish().getX() * coefficient + coefficient * 0.5, message.getBulletFinish().getY() * coefficient + coefficient * 0.5);
            }
            if (message.getBulletStart().getX() - message.getBulletFinish().getX() < 0) {//right shot
                path = new Line(message.getBulletStart().getX() * coefficient + coefficient, message.getBulletStart().getY() * coefficient + coefficient * 0.5, message.getBulletFinish().getX() * coefficient + coefficient * 0.5, message.getBulletFinish().getY() * coefficient + coefficient * 0.5);
            }
            if (message.getBulletStart().getY() - message.getBulletFinish().getY() > 0) {//up shot
                bullet.setRotate(-90);
                path = new Line(message.getBulletStart().getX() * coefficient + coefficient * 0.5, message.getBulletStart().getY() * coefficient - coefficient * 0.25, message.getBulletFinish().getX() * coefficient + coefficient * 0.5, message.getBulletFinish().getY() * coefficient + coefficient * 0.5);
            }
            if (message.getBulletStart().getY() - message.getBulletFinish().getY() < 0) {//down shot
                bullet.setRotate(90);
                path = new Line(message.getBulletStart().getX() * coefficient + coefficient * 0.5, message.getBulletStart().getY() * coefficient + coefficient * 1.25, message.getBulletFinish().getX() * coefficient + coefficient * 0.5, message.getBulletFinish().getY() * coefficient + coefficient * 0.5);
            }
            PathTransition pathTransition = new PathTransition(Duration.seconds(1), path, bullet);
            pathTransition.play();
            pathTransition.setOnFinished((w) -> {
                bullet.setVisible(false);
                rootLayout.getChildren().remove(bullet);
                for (ImageView image : anotherPlayers) {
                    rootLayout.getChildren().remove(image);
                }
                anotherPlayers.clear();
                if (maze.getFirstPlayer().contains("Gun")) {
                    gun.setVisible(true);
                } else {
                    gun.setVisible(false);
                }
                Item gunMaze = null;
                for (Item i : maze.getItems()) {
                    if (i.getName().equals("Gun")) gunMaze = i;
                }
                updateImageInMaze(gunMaze, gunInMaze);
                //add on cover icon players
                Position positionMonster = null;
                for (int i = 1; i < maze.getPlayers().size(); i++) {
                    if (maze.getPlayers().get(i).getId() == -1) {
                        positionMonster = maze.getPlayers().get(i);
                    } else {
                        ImageView imageView = new ImageView(KeyListener.class.getResource("/anotherPlayer.png").toString());
                        imageView.setFitHeight(coefficient);
                        imageView.setFitWidth(coefficient);
                        updateImageInMaze(maze.getPlayers().get(i), imageView);
                        anotherPlayers.add(imageView);
                        rootLayout.getChildren().add(imageView);
                    }
                }
                //add on cover monster
                updateImageInMaze(positionMonster, monster);
            });
        }
    }

    private void simpleCall(ServerMessage message) throws InterruptedException {
        maze = message.getMaze();
        messageFlag = false;
        //delete old positions another players
        for (ImageView image : anotherPlayers) {
            rootLayout.getChildren().remove(image);
        }
        anotherPlayers.clear();
        //change my position
        if (human.getX() != maze.getFirstPlayer().getX() * coefficient && human.getX() + coefficient != maze.getFirstPlayer().getX() * coefficient && human.getX() - coefficient != maze.getFirstPlayer().getX() * coefficient) {
            System.out.println("Fade");//todo
            FadeTransition fadeTransition = new FadeTransition();
            fadeTransition.setNode(human);
            fadeTransition.setFromValue(1.);
            fadeTransition.setToValue(0.);
            fadeTransition.setDuration(Duration.seconds(1));
            fadeTransition.play();
            fadeTransition.setOnFinished((w) -> {
                human.setX(maze.getFirstPlayer().getX() * coefficient);
                human.setY(maze.getFirstPlayer().getY() * coefficient);
                FadeTransition fadeTransition2 = new FadeTransition();
                fadeTransition2.setNode(human);
                fadeTransition2.setFromValue(0.);
                fadeTransition2.setToValue(1.);
                fadeTransition2.setDuration(Duration.seconds(1));
                fadeTransition2.setOnFinished((a) -> {
                    updateCoverMaze();
                    updateIcon();
                    messageFlag = message.amIGoingNow();
                });
                fadeTransition2.play();
            });

        } else if (human.getX() != maze.getFirstPlayer().getX() * coefficient
                || human.getY() != maze.getFirstPlayer().getY() * coefficient) {
            Line path = new Line(human.getX() + coefficient * 0.5,
                    human.getY() + coefficient * 0.5,
                    maze.getFirstPlayer().getX() * coefficient + coefficient * 0.5,
                    maze.getFirstPlayer().getY() * coefficient + coefficient * 0.5);
            PathTransition pathTransition = new PathTransition(Duration.seconds(0.5), path, human);
            pathTransition.play();
            human.setX(maze.getFirstPlayer().getX() * coefficient);
            human.setY(maze.getFirstPlayer().getY() * coefficient);
            pathTransition.setOnFinished((w) -> {
                updateCoverMaze();
                updateIcon();
                System.out.println("Update flag "+ human.getX()/coefficient + " " + human.getY()/coefficient);//todo
                messageFlag = message.amIGoingNow();
            });
        } else {
            human.setX(maze.getFirstPlayer().getX() * coefficient);
            human.setY(maze.getFirstPlayer().getY() * coefficient);
            updateCoverMaze();
            updateIcon();
            messageFlag = message.amIGoingNow();
        }

    }

    private void updateIcon() {
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
        //add on cover icon players
        Position positionMonster = null;
        for (int i = 1; i < maze.getPlayers().size(); i++) {
            if (maze.getPlayers().get(i).getId() == -1) {
                positionMonster = maze.getPlayers().get(i);
            } else {
                ImageView imageView = new ImageView(KeyListener.class.getResource("/anotherPlayer.png").toString());
                imageView.setFitHeight(coefficient);
                imageView.setFitWidth(coefficient);
                updateImageInMaze(maze.getPlayers().get(i), imageView);
                anotherPlayers.add(imageView);
                rootLayout.getChildren().add(imageView);
            }
        }
        //add on cover monster
        updateImageInMaze(positionMonster, monster);
    }

    private void updateCoverMaze() {
        //delete line
        rootLayout.getChildren().removeIf(node -> node instanceof Line && node.getId() == null);
        //draw line
        for (Field field : maze.getCover().getCov()) {
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
            rootLayout.getChildren().add(up);
            rootLayout.getChildren().add(down);
            rootLayout.getChildren().add(left);
            rootLayout.getChildren().add(right);
        }
    }

    private void firstCall(ServerMessage message) {
        maze = message.getMaze();
        messageFlag = message.amIGoingNow();
        coefficient = rootLayout.getHeight() / maze.getSize();
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
        gun.setFitHeight(rootLayout.getHeight() / 6);
        gun.setFitWidth(rootLayout.getHeight() / 6);
        gun.setX(rootLayout.getHeight() + ((rootLayout.getWidth() - rootLayout.getHeight()) / 2 - gun.getFitWidth()) / 2);
        gun.setY(rootLayout.getHeight() / 6 * 2.2);
        key = (ImageView) findElementByID("key", rootLayout);
        key.setFitHeight(rootLayout.getHeight() / 6);
        key.setFitWidth(rootLayout.getHeight() / 6);
        key.setX(rootLayout.getHeight() + (rootLayout.getWidth() - rootLayout.getHeight()) / 2 + ((rootLayout.getWidth() - rootLayout.getHeight()) / 2 - key.getFitWidth()) / 2);
        key.setY(rootLayout.getHeight() / 3);
        label = (Label) findElementByID("infLabel", rootLayout);
        updateCoverMaze();
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
