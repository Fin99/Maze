package com.fin;

import com.fin.game.cover.Direction;
import com.fin.game.maze.Maze;
import com.fin.game.maze.MazeImplDefault;
import com.fin.game.player.Player;
import com.fin.game.player.Position;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
                    processRequest();
                }
            }
        } catch (IOException e) {
            deletePlayer();
        }
    }

    private void processRequest() throws IOException {
        if (server.playerMoveNow.equals(client)) {
            String typeRequest = (String) readFromByteArray(is);
            if (typeRequest.equals("Move")) {
                move();
            } else if (typeRequest.equals("Shot")) {
                shot();
            } else if (typeRequest.equals("Update maze")) {
                server.updateMaze.replace(client, true);
                checkAndUpdateMaze();
                move();
            }
            try {
                server.notify();
                server.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void move() throws IOException {
        Direction direction = (Direction) readFromByteArray(is);
        server.maze.go(direction, idPlayer);
        Player player = null;
        for (Player pl : server.maze.getPlayers()) {
            if (pl.getId() == idPlayer) player = pl;
        }
        if (player.getX() == server.mazeSize - 1 && player.getY() == server.mazeSize - 1 && player.contains("Key")) {
            win();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            updateMaze();
            processRequest();
        }
        nextPlayer();
        updatePlayers();
    }

    private void win() {
        synchronized (server) {
            Socket disconnect = null;
            try {
                for (Socket player : server.playersSocket) {
                    disconnect = player;
                    DataOutputStream os = new DataOutputStream(player.getOutputStream());
                    writeToByteArray(os, null);
                    if (server.playersId.get(server.playersSocket.indexOf(player)) == idPlayer) {
                        writeToByteArray(os, true);
                    } else {
                        writeToByteArray(os, false);
                    }
                    writeToByteArray(os, null);
                    writeToByteArray(os, null);

                }
            } catch (IOException e) {
                System.err.println("Player disconnected (" + disconnect.getInetAddress() + ":" + disconnect.getPort() + ")");
            }
        }
    }

    private void shot() throws IOException {
        Direction direction = (Direction) readFromByteArray(is);
        Player player = null;
        for (Player p : server.maze.getPlayers()) {
            if (p.getId() == idPlayer) player = p;
        }
        nextPlayer();
        Position position = server.maze.shot(idPlayer, direction);
        updatePlayers(player, position);
    }

    private void checkAndUpdateMaze() {
        for (Boolean vote : server.updateMaze.values()) {
            if (!vote) return;
        }
        updateMaze();
    }

    private void updateMaze() {
        server.maze = MazeImplDefault.generateMaze(server.mazeSize);
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < server.playersId.size(); i++) {
            Player player = new Player(i, i, server.mazeSize, server.playersId.get(i));
            players.add(player);
        }
        server.maze.addAllPlayer(players);
        server.iteratorPlayers = server.playersSocket.iterator();
        server.playerMoveNow = server.iteratorPlayers.next();
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
                    Maze start = server.maze.start(0, 0);
                    idPlayer = start.getFirstPlayer().getId();
                    server.playersId.add(idPlayer);
                    server.updateMaze.put(client, false);
                    nextPlayer();
                    writeToByteArray(os, start);
                    writeToByteArray(os, server.playerMoveNow.equals(client));
                    writeToByteArray(os, null);
                    writeToByteArray(os, null);
                    updatePlayers();
                    System.out.println("Подключен клиент..." + client.getInetAddress() + ":" + client.getPort());
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
            System.out.println(server.playerMoveNow.getPort());//
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