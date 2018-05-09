package com.fin;

import com.fin.game.cover.Direction;
import com.fin.game.maze.Maze;
import com.fin.game.player.Item;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;


public class KeyListener implements EventHandler<KeyEvent> {
    private AnchorPane layout;
    private Connect server;
    private Maze maze;

    public KeyListener(AnchorPane rootLayout, Connect server) {
        layout = rootLayout;
        this.server = server;
        maze = (Maze) server.waitResponse();
        GridPane cover = null;
        for (Node nodes : layout.getChildren()) {
            if (nodes.getId() != null && nodes.getId().equals("cover")) cover = (GridPane) nodes;
        }
        for (int i = 0; i < maze.getSize(); i++) {
            for (int j = 0; j < maze.getSize(); j++) {
                cover.add(new Label(), i, j);
            }
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPrefWidth(600 / maze.getSize());
            cover.getColumnConstraints().add(columnConstraints);
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPrefHeight(600 / maze.getSize());
            cover.getRowConstraints().add(rowConstraints);
        }
        ImageView human = null;
        for (Node nodes : cover.getChildren()) {
            if (nodes.getId() != null && nodes.getId().equals("human")) human = (ImageView) nodes;
        }
        human.setFitHeight(600 / maze.getSize());
        human.setFitWidth(600 / maze.getSize());
        ImageView gunInMaze = null;
        for (Node nodes : cover.getChildren()) {
            if (nodes.getId() != null && nodes.getId().equals("gunInMaze")) gunInMaze = (ImageView) nodes;
        }
        gunInMaze.setFitHeight(600 / maze.getSize());
        gunInMaze.setFitWidth(600 / maze.getSize());
        ImageView keyInMaze = null;
        for (Node nodes : cover.getChildren()) {
            if (nodes.getId() != null && nodes.getId().equals("keyInMaze")) keyInMaze = (ImageView) nodes;
        }
        keyInMaze.setFitHeight(600 / maze.getSize());
        keyInMaze.setFitWidth(600 / maze.getSize());
        ImageView monster = null;
        for (Node nodes : cover.getChildren()) {
            if (nodes.getId() != null && nodes.getId().equals("monster")) monster = (ImageView) nodes;
        }
        monster.setFitHeight(600 / maze.getSize());
        monster.setFitWidth(600 / maze.getSize());
    }

    @Override
    public void handle(KeyEvent event) {
        GridPane cover = null;
        for (Node nodes : layout.getChildren()) {
            if (nodes.getId() != null && nodes.getId().equals("cover")) cover = (GridPane) nodes;
        }
        ImageView human = null;
        for (Node nodes : cover.getChildren()) {
            if (nodes.getId() != null && nodes.getId().equals("human")) human = (ImageView) nodes;
        }
        switch (event.getCode()) {
            case RIGHT:
                server.sendRequest(true, Direction.RIGHT);
                break;
            case LEFT:
                server.sendRequest(true, Direction.LEFT);
                break;
            case UP:
                server.sendRequest(true, Direction.UP);
                break;
            case DOWN:
                server.sendRequest(true, Direction.DOWN);
                break;
            default:
                switch (event.getCode()) {
                    case W:
                        server.sendRequest(false, Direction.UP);
                        break;
                    case A:
                        server.sendRequest(false, Direction.LEFT);
                        break;
                    case S:
                        server.sendRequest(false, Direction.DOWN);
                        break;
                    case D:
                        server.sendRequest(false, Direction.RIGHT);
                        break;
                }
                maze = (Maze) server.waitResponse();
                server.waitResponse();
                cover.getChildren().remove(human);
                cover.add(human, maze.getFirstPlayer().getX(), maze.getFirstPlayer().getY());
        }
        ImageView key = null;
        for (Node nodes : layout.getChildren()) {
            if (nodes.getId() != null && nodes.getId().equals("key")) key = (ImageView) nodes;
        }
        if (maze.getFirstPlayer().contains("Key")) {
            key.setVisible(true);

        } else {
            key.setVisible(false);
        }
        ImageView gun = null;
        for (Node nodes : layout.getChildren()) {
            if (nodes.getId() != null && nodes.getId().equals("gun")) gun = (ImageView) nodes;
        }
        if (maze.getFirstPlayer().contains("Gun")) {
            gun.setVisible(true);
        } else {
            gun.setVisible(false);
        }
        //
        ImageView keyInMaze = null;
        for (Node nodes : cover.getChildren()) {
            if (nodes.getId() != null && nodes.getId().equals("keyInMaze")) keyInMaze = (ImageView) nodes;
        }
        Item keyMaze = null;
        for (Item i: maze.getItems()) {
            if(i.getName().equals("Key")) keyMaze = i;
        }
        if (keyMaze!=null) {
            cover.getChildren().remove(keyInMaze);
            cover.add(keyInMaze, keyMaze.getX(), keyMaze.getY());
            keyInMaze.setVisible(true);
        } else {
            keyInMaze.setVisible(false);
        }
        //
        ImageView gunInMaze = null;
        for (Node nodes : cover.getChildren()) {
            if (nodes.getId() != null && nodes.getId().equals("gunInMaze")) gunInMaze = (ImageView) nodes;
        }
        Item gunMaze = null;
        for (Item i: maze.getItems()) {
            if(i.getName().equals("Gun")) gunMaze = i;
        }
        if (gunMaze!=null) {
            cover.getChildren().remove(gunInMaze);
            cover.add(gunInMaze, gunMaze.getX(), gunMaze.getY());
            gunInMaze.setVisible(true);
        } else {
            gunInMaze.setVisible(false);
        }
        for (int i = 1; i < maze.getPlayers().size(); i++) {
            if (maze.getPlayers().get(i).getId() == -1) {
                ImageView monster = null;
                for (Node nodes : cover.getChildren()) {
                    if (nodes.getId() != null && nodes.getId().equals("monster")) monster = (ImageView) nodes;
                }
                cover.getChildren().remove(monster);
                cover.add(monster, maze.getSize()-1, maze.getSize()-1);
                monster.setVisible(true);
            }
        }
    }
}
