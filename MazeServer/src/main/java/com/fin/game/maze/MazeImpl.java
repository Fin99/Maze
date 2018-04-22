package com.fin.game.maze;

import java.util.Arrays;
import java.util.Collections;

public class MazeImpl implements Maze {
    int personX;
    int personY;
    int[][] maze;
    static int size;

    public void setMaze(int[][] maze) {
        this.maze = maze;
        size = maze.length;
    }

    public int[][] getMaze() {
        return maze;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        if (size != this.size) {
            this.size = size;
            maze = new int[size][size];
        }
    }

    String display() {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < size; i++) {
            // draw the north edge
            for (int j = 0; j < size; j++) {

                if (j == 0 && i == 0) {
                    out.append("    ");
                } else {
                    out.append((maze[j][i] & 1) == 0 ? "+---" : "+   ");
                }

            }
            out.append("+\n");
            // draw the west edge
            for (int j = 0; j < size; j++) {
                if (j == personX && i == personY) {
                    out.append((maze[j][i] & 8) == 0 ? "| @ " : "  @ ");
                } else {
                    out.append((maze[j][i] & 8) == 0 ? "|   " : "    ");
                }
            }
            out.append("|\n");
        }
        // draw the bottom line
        for (int j = 0; j < size - 1; j++) {
            out.append("+---");
        }
        out.append("+");
        return out.toString();
    }

    boolean canIGo(int dx, int dy) {
        if (personX + dx < size && personX + dx >= 0 && personY + dy < size && personY + dy >= 0) {
            if (dx != 0) {
                if (dx == 1) {
                    if ((maze[personX + dx][personY] & 8) == 8) {
                        personX += dx;
                        return true;
                    }
                } else {
                    if ((maze[personX][personY] & 8) == 8) {
                        personX += dx;
                        return true;
                    }
                }
            } else if (dy != 0) {
                if (dy == 1) {
                    //вниз | вверх
                    if ((maze[personX][personY + dy] & 1) == 1) {
                        personY += dy;
                        return true;
                    }
                } else {
                    if ((maze[personX][personY] & 1) == 1) {
                        personY += dy;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String go(String a) {
        switch (a) {
            case ("w"):
                if (canIGo(0, -1)) {
                    return display() + isWin();
                }
                break;
            case ("a"):
                if (canIGo(-1, 0)) {
                    return display() + isWin();
                }
                break;
            case ("s"):
                if (canIGo(0, 1)) {
                    return display() + isWin();
                }
                break;
            case ("d"):
                if (canIGo(1, 0)) {
                    return display() + isWin();
                }
                break;
            default:
                return display() + "\nУправление клавишами: w, a, s, d                     ";
        }
        return display() + "\nНеудачный ход                    ";
    }

    String isWin() {
        if (personX == size - 1 && personY == size - 1) return "\nВы выиграли!                                    ";
        return "\n                                                                       ";
    }

    public String start() {
        return display() + isWin();
    }

    @Override
    public String toString() {
        return display();
    }

    public void generateMaze(int cx, int cy) {
        DIR[] dirs = DIR.values();
        Collections.shuffle(Arrays.asList(dirs));
        for (DIR dir : dirs) {
            int nx = cx + dir.dx;
            int ny = cy + dir.dy;
            if (between(nx, size) && between(ny, size)
                    && (maze[nx][ny] == 0)) {
                maze[cx][cy] |= dir.bit;
                maze[nx][ny] |= dir.opposite.bit;
                generateMaze(nx, ny);
            }
        }
    }

    private static boolean between(int v, int upper) {
        return (v >= 0) && (v < upper);
    }

    private enum DIR {
        N(1, 0, -1), S(2, 0, 1), E(4, 1, 0), W(8, -1, 0);
        private final int bit;
        private final int dx;
        private final int dy;
        private DIR opposite;

        // use the static initializer to resolve forward references
        static {
            N.opposite = S;
            S.opposite = N;
            E.opposite = W;
            W.opposite = E;
        }

        private DIR(int bit, int dx, int dy) {
            this.bit = bit;
            this.dx = dx;
            this.dy = dy;
        }
    }
}