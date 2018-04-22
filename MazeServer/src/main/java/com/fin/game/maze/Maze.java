package com.fin.game.maze;

public interface Maze {
    String start();
    String go(String a);
    int[][] getMaze();
    void setMaze(int[][] a);
    int getSize();
    void setSize(int size);
    void generateMaze(int a, int b);
}
