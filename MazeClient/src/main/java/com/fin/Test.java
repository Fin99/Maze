package com.fin;

import com.fin.game.cover.Direction;
import com.fin.game.maze.Maze;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Test {
    static Connect connect;

    static {
        try {
            connect = new Connect(new Socket(InetAddress.getLocalHost(), 2600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Maze maze;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            maze = (Maze) connect.waitResponse();
            System.out.println("receive maze");
            if ((Boolean) connect.waitResponse()) {
                System.out.println("i am went");
                Direction direction = null;
                switch (scanner.next()) {
                    case "w":
                        direction = Direction.UP;
                        break;
                    case "a":
                        direction = Direction.LEFT;
                        break;
                    case "s":
                        direction = Direction.DOWN;
                        break;
                    case "d":
                        direction = Direction.RIGHT;
                        break;
                }
                connect.sendRequest(false, direction);
                System.out.println("i went");
            } else {
                System.out.println("i amn`t went");
            }
        }
    }
}
