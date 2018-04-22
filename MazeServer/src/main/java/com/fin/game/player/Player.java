package com.fin.game.player;

import com.fin.game.cover.Cover;

public class Player {
    private int playerX;
    private int playerY;
    private boolean gun;
    private boolean key;
    private Character icon;
    private Cover cover;

    public Player(char icon, int size) {
        this.icon = icon;
        cover = new Cover(size);
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

    public Cover getCover() {
        return cover;
    }

    public boolean isKey() {
        return key;
    }

    public void setKey(boolean key) {
        this.key = key;
    }
}
