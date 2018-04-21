package com.fin.game;

public class Player {
    private int playerX;
    private int playerY;
    private boolean gun;
    private Character icon;
    int[][] checked;

    public Player(char icon) {
        this.icon = icon;
    }

    public int getPlayerX() {
        return playerX;
    }

    public void setPlayerX(int playerX) {
        this.playerX = playerX;
    }

    public int getPlayerY() {
        return playerY;
    }

    public void setPlayerY(int playerY) {
        this.playerY = playerY;
    }

    public Character getIcon() {
        return icon;
    }

    public boolean isGun() {
        return gun;
    }

    public void setGun(boolean gun) {
        this.gun = gun;
    }
}
