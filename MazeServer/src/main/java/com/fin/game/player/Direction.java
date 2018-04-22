package com.fin.game.player;

public enum Direction {
    Up,
    Down,
    Right,
    Left;

    public char getIcon() {
        switch (this) {
            case Up:
            case Down:
                return '|';
            case Left:
            case Right:
                return '-';
        }
        return 0;
    }

    public String getDir() {
        switch (this) {
            case Up:
                return "w";
            case Right:
                return "d";
            case Left:
                return "a";
            case Down:
                return "s";
        }
        return null;
    }
}