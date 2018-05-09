package com.fin;

import com.fin.game.cover.Direction;
import com.fin.game.maze.Maze;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    private DataOutputStream os;
    private DataInputStream is;
    private Socket client;
    private int idPlayer;

    ClientThread(Socket client) {
        try {
            os = new DataOutputStream(client.getOutputStream());
            is = new DataInputStream(client.getInputStream());
            this.client = client;
            System.out.println("Подключен клиент..." + client.getInetAddress() + ":" + client.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            if (Server.iteratorPlayers == null) {
                Server.iteratorPlayers = Server.players.iterator();
                Server.playerMoveNow = Server.iteratorPlayers.next();
            }
            Maze start = Server.maze.start(0, 0);
            idPlayer = start.getFirstPlayer().getId();
            writeToByteArray(os, start);
            while (true) {
                while (Server.playerMoveNow != client) {
                }
                Boolean isShot = (Boolean) readFromByteArray(is);
                Direction direction = (Direction) readFromByteArray(is);
                if (isShot) {
                    Server.maze.shot(idPlayer, direction);
                    nextPlayer();
                } else {
                    Server.maze.go(direction, idPlayer);
                    nextPlayer();
                }
                updatePlayers();
            }
        } catch (IOException e) {
            Server.iteratorPlayers.remove();
            Server.maze.deletePlayer(idPlayer);
            nextPlayer();
        }
    }

    private void updatePlayers() {
        Socket disconnect = null;
        try {
            for (Socket player : Server.players) {
                disconnect = player;
                DataOutputStream os = new DataOutputStream(player.getOutputStream());
                writeToByteArray(os, Server.maze.go(null, idPlayer));
                writeToByteArray(os, Server.playerMoveNow.equals(player));
            }
        } catch (IOException e) {
            System.err.println("Player disconnected (" + disconnect.getInetAddress() + ":" + disconnect.getPort() + ")");
        }
    }

    private void nextPlayer() {
        if (Server.players.size() == 0) System.exit(0);
        if (Server.iteratorPlayers.hasNext()) {
            Server.playerMoveNow = Server.iteratorPlayers.next();
        } else {
            Server.iteratorPlayers = Server.players.iterator();
            Server.playerMoveNow = Server.iteratorPlayers.next();
        }
    }

    public void writeToByteArray(DataOutputStream stream, Object element) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(element);
        byte[] buffObj = baos.toByteArray();
        stream.writeInt(buffObj.length);
        stream.write(buffObj);
        stream.flush();
    }

    public Object readFromByteArray(DataInputStream stream) throws IOException {
        int lengthBuff = stream.readInt();
        byte[] buff = new byte[lengthBuff];
        stream.readFully(buff, 0, lengthBuff);
        ByteArrayInputStream bais = new ByteArrayInputStream(buff);
        ObjectInputStream in = new ObjectInputStream(bais);
        try {
            return in.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    //    @Override
//    public void run() {
//        try {
//            os.write(startNewClient());
//            while (true) {
//                if (is.available() > 0) {
//                    String response;
//
//                    //convert request player in String
//                    byte[] string = new byte[is.available()];
//                    is.read(string);
//                    String request = (String) readFromByteArray(string);
//                    //when you click the arrows sent extra information ignore it
//                    if (isNoise(request)) {
//                        if (idPlayer == Server.movePlayer.get() && Server.countClient.get()!=0) message = "Ваш ход!";
//                        response = Server.maze.show(message, idPlayer);
//                    } else {
//                        //check this if the player has to move
//                        if (idPlayer == Server.movePlayer.get()) {
//                            //making a move
//                            response = move(request);
//                        } else {
//                            message = "Сейчас не ваш ход";
//                            //form the response
//                            response = Server.maze.show(message, idPlayer);
//                        }
//                    }
//
//                    os.write(writeToByteArray(response));
//                }
//            }
//        } catch (IOException e) {
//            Server.countClient.decrementAndGet();
//        }
//    }
//
//    //when you click the arrows sent extra information ignore it
//    private boolean isNoise(String s) {
//        if (s.equals("show")) return true;
//        char[] a = s.toCharArray();
//        return a[0] == 27 || a[0] == 91;
//    }
//
//    //describes the player's move
//    private String move(String request) {
//        String result;
//        Direction shot = isShot(request); //check whether the player made a shot
//        if (shot == null) {
//            result = Server.maze.go(request, idPlayer);
//            if (!result.contains("Управление клавишами: w, a, s, d")) {
//                nextMove();
//            }
//            message = result.split("\n")[result.split("\n").length - 1];
//        } else {
//            //check whether the shot was successful
//            if (Server.maze.shot(idPlayer, shot)) {
//                message = "Пау";
//                //form the response
//                result = Server.maze.show(message, idPlayer);
//                nextMove();
//            } else {
//                message = "Сначала достаньте пистолет!";
//                //form the response
//                result = Server.maze.show(message, idPlayer);
//            }
//        }
//        return result;
//    }
//
//    //Verify the player has pressed a key from the WASD group or one of the arrows
//    private Direction isShot(String request) {
//        if (request.toCharArray().length >= 1) {
//            switch (request.toCharArray()[0]) {
//                case 66:
//                    return Direction.Down;
//                case 65:
//                    return Direction.Up;
//                case 68:
//                    return Direction.Left;
//                case 67:
//                    return Direction.Right;
//            }
//        }
//        return null;
//    }
//
//    //add a new player if the number of players on the server does not exceed the maximum number of players
//    private byte[] startNewClient() {
//        if (idPlayer < Server.maxClient) {
//            if (idPlayer == 0) {
//                Server.maze.addPlayer(0, 0, (char) (idPlayer + 48));
//            } else {
//                Server.maze.addPlayer(Server.maze.getSize() - 1, 0, (char) (idPlayer + 48));
//            }
//            return writeToByteArray(Server.maze.start(idPlayer));
//        } else {
//            return writeToByteArray("Ожидайте. На сервере достигнуто максимальное кол-во людей");
//        }
//    }
//
//    //sets who moves next.
//    private void nextMove() {
//        if (idPlayer == Server.countClient.get()) {
//            Server.movePlayer.set(0);
//        } else {
//            Server.movePlayer.incrementAndGet();
//        }
//    }
//
//    private byte[] writeToByteArray(Object element) {
//        try {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ObjectOutputStream out = new ObjectOutputStream(baos);
//            out.writeObject(element);
//            return baos.toByteArray();
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("Error in conversion in byte array");
//        }
//    }
//
//    private Object readFromByteArray(byte[] bytes) {
//        try {
//            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
//            ObjectInputStream in = new ObjectInputStream(bais);
//            return in.readObject();
//        } catch (ClassNotFoundException | IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("Error in conversion in object");
//        }
//    }
}