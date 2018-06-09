package com.fin.game.player;

import java.io.Serializable;

public interface Position extends Serializable {
    int getX();
    void setX(int x);
    int getY();
    void setY(int y);
}
