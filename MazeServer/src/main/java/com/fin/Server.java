package com.fin;

import com.fin.game.maze.Maze;
import com.fin.game.maze.MazeImplDefault;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class Server {
    int maxClient;
    int mazeSize;
    volatile Maze maze;
    volatile List<Socket> playersSocket;
    volatile List<Integer> playersId;
    volatile Socket playerMoveNow;
    volatile Iterator<Socket> iteratorPlayers;

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    private void start() {
        System.out.println("Вас приветсвует сервер Maze.");
        //create maze and initialize rest of field
        init();
        try (ServerSocket server = new ServerSocket(selectPort())) {
            new NotifierThread(this).start();
            Socket client;
            while (true) {
                client = server.accept();
                new ClientThread(client, this).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //allows the client to select a size maze
    private int selectMazeSize() {
        System.out.print("Выберите размер лабиринта. Лабиринт представляет собой квадрат, вы должны выбрать размер стороны квадрата. Допусимые размеры 5-15: ");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            int size = 0;
            try {
                size = Integer.parseInt(scanner.next());

                if (size > 4 && size < 16) {
                    System.out.println("Размер лабиринта установлен.");
                    return size;
                }
                System.out.print("Допустимые размеры 5-15: ");
            } catch (NumberFormatException e) {
                System.out.print("Выбранное значение должно быть целым числом: ");
            }
        }
    }

    //allows the client to select a max player on server
    private int selectMaxClient() {
        System.out.print("Выберите максимальное количество игроков, которые смогут одновременно подключиться к серверу. Допустимое количество игроков 1-3: ");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            int count = 0;
            try {
                count = Integer.parseInt(scanner.next());
                if (count > 0 && count < 4) {
                    System.out.println("Максимальное количество игроков для сервера установлено.");
                    return count;
                }
                System.out.print("Допустимые количество игроков 1-3: ");
            } catch (NumberFormatException e) {
                System.out.print("Выбранное значение должно быть целым числом: ");
            }
        }
    }

    //allows the client to select a port
    private int selectPort() {
        System.out.print("Выберите порт на котором вы хотите расположить сервер. Допустимые значения больше тысячи: ");
        Scanner scanner = new Scanner(System.in);
        ServerSocket server;
        while (true) {
            int port = 0;
            try {
                port = Integer.parseInt(scanner.next());
                if (port > 1000) {
                    server = new ServerSocket(port);
                    server.close();
                    System.out.println("Сервер был удачно запущен...");
                    return port;
                }

            } catch (IOException e) {
                System.out.print("Этот порт уже занят выберите другой порт: ");

            } catch (NumberFormatException e) {
                System.out.print("Выбранное значение должно быть целым числом: ");
            }
            System.out.print("Номер порта должен быть больше 1000: ");
        }

    }

    private void init() {
        maxClient = selectMaxClient();
        mazeSize = selectMazeSize();
        maze = MazeImplDefault.generateMaze(mazeSize);
        playersSocket = new ArrayList<>();
        playersId = new ArrayList<>();
    }


}