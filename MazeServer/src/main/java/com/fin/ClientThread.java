package com.fin;

import com.fin.game.Direction;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    private DataOutputStream os;
    private DataInputStream is;
    private String message = "";
    private int idPlayer;

    ClientThread(Socket client) {
        try {
            idPlayer = Server.countClient.incrementAndGet();
            os = new DataOutputStream(client.getOutputStream());
            is = new DataInputStream(client.getInputStream());
            System.out.println("Подключен клиент..." + idPlayer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            os.write(startNewClient());
            while (true) {
                if (is.available() > 0) {
                    String response;

                    //convert request player in String
                    byte[] string = new byte[is.available()];
                    is.read(string);
                    String request = (String) readFromByteArray(string);
                    //when you click the arrows sent extra information ignore it
                    if (isNoise(request)) {
                        response = Server.maze.show(message, idPlayer);
                    } else {
                        //check this if the player has to move
                        if (idPlayer == Server.movePlayer.get()) {
                            //making a move
                            response = move(request);
                            System.out.println("Сейчас ход игрока " + Server.movePlayer.get());
                        } else {
                            message = "Сейчас не ваш ход";
                            //form the response
                            response = Server.maze.show(message, idPlayer);
                        }
                    }

                    os.write(writeToByteArray(response));
                }
            }
        } catch (IOException e) {
            Server.countClient.decrementAndGet();
        }
    }

    //when you click the arrows sent extra information ignore it
    private boolean isNoise(String s) {
        if (s.equals("show")) return true;
        char[] a = s.toCharArray();
        return a[0] == 27 || a[0] == 91;
    }

    //describes the player's move
    private String move(String request) {
        String result;
        Direction shot = isShot(request); //check whether the player made a shot
        if (shot == null) {
            result = Server.maze.go(request, idPlayer);
            if (!result.contains("Управление клавишами: w, a, s, d")) {
                nextMove();
            }
            message = result.split("\n")[result.split("\n").length - 1];
        } else {
            //check whether the shot was successful
            if (Server.maze.shot(idPlayer, shot)) {
                message = "Пау";
                //form the response
                result = Server.maze.show(message, idPlayer);
                nextMove();
            } else {
                message = "Сначала достаньте пистолет!";
                //form the response
                result = Server.maze.show(message, idPlayer);
            }
        }
        return result;
    }

    //Verify the player has pressed a key from the WASD group or one of the arrows
    private Direction isShot(String request) {
        if (request.toCharArray().length >= 1) {
            switch (request.toCharArray()[0]) {
                case 66:
                    return Direction.Down;
                case 65:
                    return Direction.Up;
                case 68:
                    return Direction.Left;
                case 67:
                    return Direction.Right;
            }
        }
        return null;
    }

    //add a new player if the number of players on the server does not exceed the maximum number of players
    private byte[] startNewClient() {
        if (idPlayer < Server.maxClient) {
            if (idPlayer == 0) {
                Server.maze.addPlayer(0, 0, (char) (idPlayer + 48));
            } else {
                Server.maze.addPlayer(Server.maze.getSize() - 1, 0, (char) (idPlayer + 48));
            }
            return writeToByteArray(Server.maze.start(idPlayer));
        } else {
            return writeToByteArray("Ожидайте. На сервере достигнуто максимальное кол-во людей");
        }
    }

    //sets who moves next.
    private void nextMove() {
        if (idPlayer == Server.countClient.get()) {
            Server.movePlayer.set(0);
        } else {
            Server.movePlayer.incrementAndGet();
        }
    }

    private byte[] writeToByteArray(Object element) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeObject(element);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error in conversion in byte array");
        }
    }

    private Object readFromByteArray(byte[] bytes) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream in = new ObjectInputStream(bais);
            return in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error in conversion in object");
        }
    }
}