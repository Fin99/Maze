package com.fin;

import com.fin.game.cover.Direction;
import com.fin.game.maze.Maze;
import com.fin.game.player.Player;
import com.fin.game.player.Position;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread implements Serializable {
    private final Server server;
    private DataOutputStream os;
    private DataInputStream is;
    private Socket client;
    private int idPlayer;

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
                            Player player = null;
                            for (Player p : server.maze.getPlayers()) {
                                if (p.getId() == idPlayer) player = p;
                            }
                            nextPlayer();
                            Position position = server.maze.shot(idPlayer, direction);
                            updatePlayers(player, position);
                        } else {
                            server.maze.go(direction, idPlayer);
                            nextPlayer();
                            updatePlayers();
                        }
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
                    writeToByteArray(os, null);
                    writeToByteArray(os, null);
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
                    writeToByteArray(os, null);
                    writeToByteArray(os, null);
                }
            } catch (IOException e) {
                System.err.println("Player disconnected (" + disconnect.getInetAddress() + ":" + disconnect.getPort() + ")");
            }
        }
    }

    private void updatePlayers(Player p, Position position) {
        synchronized (server) {
            Socket disconnect = null;
            try {
                for (Socket player : server.playersSocket) {
                    disconnect = player;
                    DataOutputStream os = new DataOutputStream(player.getOutputStream());
                    writeToByteArray(os, server.maze.go(null, server.playersId.get(server.playersSocket.indexOf(player))));
                    writeToByteArray(os, server.playerMoveNow.equals(player));
                    writeToByteArray(os, p);
                    writeToByteArray(os, position);
                }
            } catch (IOException e) {
                System.err.println("Player disconnected (" + disconnect.getInetAddress() + ":" + disconnect.getPort() + ")");
                e.printStackTrace();
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