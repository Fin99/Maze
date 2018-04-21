package com.fin.game;

public interface OnlineMaze extends Maze, ManyPlayer {
    String go(String a, int idPlayer);
    String start(int idPlayer);
    String show(String s, int idPlayer);
    boolean shot(int idPlayer, Direction direction);
}
