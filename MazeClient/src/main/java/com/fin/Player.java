package com.fin;

import com.fin.connect.Connect;
import com.fin.connect.ConnectImpl;
import com.fin.controlConsole.GameConsole;
import com.fin.controlConsole.GameConsoleImpl;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;


public class Player {

    private static GameConsole console;

    public static void main(String[] args) {
        try {
            //Attempt to connect to the server and display a welcome message
            initial();
        } catch (IOException e) {
            System.err.println("Возникла ошибка при работа с терминалом.");
            System.exit(1);
        }
        //start game
        console.game();
    }

    private static void initial() throws IOException {
        //ask the player to specify the server port
        System.out.println("Добро пожаловать в игру Maze!");
        System.out.println("Пожалуйста, разверните окно терминала на весь экран.");
        System.out.print("Укажите порт на котором расположен сервер: ");
        Connect serverConnect = new ConnectImpl(selectPort());
        console = new GameConsoleImpl(serverConnect);
        System.out.println("Установлено соединение с сервером.");
        //we deduce the training text on management of the character
        System.out.println("Ваша задача добраться до конца лабиринта. Для этого найдите ключ и убейте монстра. Удачи!\n" +
                "(Управление персонажем клавишами w, a, s, d. Чтобы произвести выстрел используйте стрелки.)");
    }

    //allows the client to select a port on which the server is located
    private static int selectPort() {
        Scanner scanner = new Scanner(System.in);
        Socket server;
        while (true) {
            int port = 0;
            try {
                port = Integer.parseInt(scanner.next());
                if (port > 1000) {
                    return port;
                } else {
                    System.out.print("Номер порта должен быть больше 1000: ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Выбранное значение должно быть целым числом: ");
            }
        }

    }
}
