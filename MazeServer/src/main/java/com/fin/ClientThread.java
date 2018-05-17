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
    private final Server server;

    ClientThread(Socket client, Server server) {
        this.server = server;
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
            connect();
            while (true) {
                synchronized (server) {
                    if (server.playerMoveNow.equals(client)) {
                        Boolean isShot = (Boolean) readFromByteArray(is);
                        Direction direction = (Direction) readFromByteArray(is);
                        if (isShot) {
                            server.maze.shot(idPlayer, direction);
                            nextPlayer();
                        } else {
                            server.maze.go(direction, idPlayer);
                            nextPlayer();
                        }
                        updatePlayers();
                        try {
                            server.notify();
                            server.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            deletePlayer();
        }
    }

    private void deletePlayer() {
        synchronized (server) {
            server.playersId.remove(idPlayer);
            server.iteratorPlayers.remove();
            nextPlayer();
            updatePlayers();
            server.maze.deletePlayer(idPlayer);
        }

    }

    private void connect() throws IOException {
        while (true) {
            synchronized (server) {
                if (server.playersSocket.size() < server.maxClient) {
                    server.playersSocket.add(client);
                    server.iteratorPlayers = server.playersSocket.iterator();
                    server.playerMoveNow = server.iteratorPlayers.next();
                    Maze start = server.maze.start(0, 0);
                    idPlayer = start.getFirstPlayer().getId();
                    server.playersId.add(idPlayer);
                    writeToByteArray(os, start);
                    writeToByteArray(os, server.playerMoveNow.equals(client));
                    return;
                }
            }
        }
    }

    private void updatePlayers() {
        synchronized (server) {
            Socket disconnect = null;
            try {
                for (Socket player : server.playersSocket) {
                    disconnect = player;
                    DataOutputStream os = new DataOutputStream(player.getOutputStream());
                    writeToByteArray(os, server.maze.go(null, server.playersId.get(server.playersSocket.indexOf(player))));
                    writeToByteArray(os, server.playerMoveNow.equals(player));
                }
            } catch (IOException e) {
                System.err.println("Player disconnected (" + disconnect.getInetAddress() + ":" + disconnect.getPort() + ")");
            }
        }
    }

    private void nextPlayer() {
        synchronized (server) {
            if (server.playersSocket.size() == 0) System.exit(0);
            if (server.iteratorPlayers.hasNext()) {
                server.playerMoveNow = server.iteratorPlayers.next();
            } else {
                server.iteratorPlayers = server.playersSocket.iterator();
                server.playerMoveNow = server.iteratorPlayers.next();
            }
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

}