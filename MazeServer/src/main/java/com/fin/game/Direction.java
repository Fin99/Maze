package com.fin.game;

public enum Direction {
    Up,
    Down,
    Right,
    Left;

    char getIcon() {
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

    String getDir() {
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