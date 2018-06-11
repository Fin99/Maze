package com.fin;

import com.fin.game.cover.Direction;
import com.fin.game.maze.Maze;
import com.fin.game.maze.MazeImplDefault;
import com.fin.game.player.Item;
import com.fin.game.player.Player;
import com.fin.game.player.Position;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

//todo handle situation when server space is over
public class ClientThread extends Thread implements Serializable {
    //logger
    private final Logger logger = LogManager.getRootLogger();
    //

    private final Server server;
    private DataOutputStream os;
    private DataInputStream is;
    private Socket client;
    private int idPlayer;

    ClientThread(Socket client, Server server) {
        logger.info("Create new ClientThread(" + client.getInetAddress().getHostAddress() + ":" + client.getPort() + ")");
        this.server = server;
        try {
            os = new DataOutputStream(client.getOutputStream());
            is = new DataInputStream(client.getInputStream());
            this.client = client;
        } catch (IOException e) {
            logger.fatal("Exception occurred when establishing communication with the client.");
        }
    }

    @Override
    public void run() {
        logger.info("Start ClientThread");
        try {
            connect();
            while (true) {
                synchronized (server) {
                    processRequest();
                }
            }
        } catch (IOException e) {
            logger.error("Player disconnected");
            synchronized (server) {
                deletePlayer();
            }
        }
    }

    private void processRequest() throws IOException {
        logger.info("Thread is waking up");
        if (server.playerMoveNow.equals(client)) {
            logger.info("Waiting of message from client...");
            ClientMessage message = (ClientMessage) readFromByteArray(is);
            logger.info("Message received.Processing of message from client...");
            logger.info("Type message from client - " + message.getType());
            if (message.getType().equals("Move")) {
                move(message.getDirection());
            } else if (message.getType().equals("Shot")) {
                shot(message.getDirection());
            } else if (message.getType().equals("Restart")) {
                logger.info("Player("+idPlayer+") voted to update game");
                server.updateMaze.replace(client, true);
                checkAndUpdateMaze();
            }
            logger.info("Processing of message is finished");
            try {
                server.notify();
                logger.info("Thread is waiting");
                server.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void move(Direction direction) {
        logger.info("Start move");
        Player player = null;
        for (Player pl : server.maze.getPlayers()) {
            if (pl.getId() == idPlayer) player = pl;
        }
        if (player == null) {
            logger.fatal("Player not found in maze");
        } else {
            Position startPosition = player.getPosition();
            server.maze.go(direction, idPlayer);
            for (Player pl : server.maze.getPlayers()) {
                if (pl.getId() == idPlayer) player = pl;
            }
            Position finishPosition = player.getPosition();
            if (startPosition.getY() == finishPosition.getY() && startPosition.getX() == finishPosition.getX()) {
                logger.info("Player(" + idPlayer + ") couldn't make a move out of the place(" + startPosition.getX() + ":" + startPosition.getY() + ")");
            } else {
                logger.info("Player(" + idPlayer + ") made a move into the place(" + finishPosition.getX() + ":" + finishPosition.getY() + ") from the place(" + startPosition.getX() + ":" + startPosition.getY() + ")");
            }
            if (player.getX() == server.mazeSize - 1 && player.getY() == server.mazeSize - 1 && player.contains("Key")) {
                logger.info("Player(" + idPlayer + ") is winner");
                win();
            } else {
                nextPlayer();
                notifyAboutMovePlayers(direction, startPosition, finishPosition);
            }
            logger.info("Finish move");
        }
    }

    private void win() {
        logger.info("Notify all players about win or lose");
        Socket disconnect = null;
        try {
            for (Socket player : server.playersSocket) {
                disconnect = player;
                DataOutputStream os = new DataOutputStream(player.getOutputStream());
                Integer playerID = server.playersId.get(server.playersSocket.indexOf(player));
                ServerMessage serverMessage = new ServerMessage(playerID == idPlayer ? "Win" : "Lose",
                        null, null, null, null, null, null, null, null);
                writeToByteArray(os, serverMessage);
            }
        } catch (IOException e) {
            logger.error("Player disconnected (" + disconnect.getInetAddress() + ":" + disconnect.getPort() + ")");
        }
        logger.info("All players is notified");
        updateMaze();
    }

    private void shot(Direction direction) {
        logger.info("Start shot");
        Player player = null;
        for (Player p : server.maze.getPlayers()) {
            if (p.getId() == idPlayer) player = p;
        }
        if (player == null) {
            logger.fatal("Player not found in maze");
        } else {
            Position startPosition = player.getPosition();
            nextPlayer();
            Position finishPosition = server.maze.shot(idPlayer, direction);
            notifyAboutShotPlayers(direction, startPosition, finishPosition);
        }
        logger.info("Finish shot");
    }

    private void checkAndUpdateMaze() {
        for (Boolean vote : server.updateMaze.values()) {
            if (!vote) return;
        }
        logger.info("Update game...");
        updateMaze();
    }

    private void updateMaze() {
        logger.info("Server reload...");
        logger.info("Server update maze...");
        server.maze = MazeImplDefault.generateMaze(server.mazeSize);
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < server.playersId.size(); i++) {
            Player player = new Player(i, i, server.mazeSize, server.playersId.get(i));
            players.add(player);
        }
        server.maze.addAllPlayer(players);
        server.iteratorPlayers = server.playersSocket.iterator();
        server.playerMoveNow = server.iteratorPlayers.next();
        logger.info("Maze updated");
        notifyAboutUpdateMaze();
        logger.info("Server reloaded");
    }

    //todo
    private void deletePlayer() {
        server.playersId.remove(idPlayer);
        server.iteratorPlayers.remove();
        logger.info("Player is removed from the maze and server list");
        nextPlayer();
        //updatePlayers();
        server.maze.deletePlayer(idPlayer);
    }

    private void connect() throws IOException {
        logger.info("Start connect");
        while (true) {
            synchronized (server) {
                logger.info("Catch monitor");
                if (server.playersSocket.size() < server.maxClient) {
                    logger.info("Server have free space");
                    server.playersSocket.add(client);
                    server.iteratorPlayers = server.playersSocket.iterator();
                    logger.info("Player added on server");
                    Maze start = server.maze.start(0, 0);
                    idPlayer = start.getFirstPlayer().getId();
                    logger.info("Player added in maze. PlayerID - " + idPlayer);
                    server.playersId.add(idPlayer);
                    server.updateMaze.put(client, false);
                    nextPlayer();
                    ServerMessage message = new ServerMessage("Resize",
                            start,
                            server.playerMoveNow.equals(client),
                            null, null, null, null,
                            positionItem(server.maze.go(null, idPlayer).getItems(), "Key"),
                            positionItem(server.maze.go(null, idPlayer).getItems(), "Gun"));
                    writeToByteArray(os, message);
                    notifyAboutAddedNewPlayer();
                    logger.info("Connect is finished");
                    return;
                }
            }
        }
    }

    private void notifyAboutAddedNewPlayer() {
        logger.info("Notify all players about added new player");
        Socket disconnect = null;
        try {
            for (Socket player : server.playersSocket) {
                disconnect = player;
                DataOutputStream os = new DataOutputStream(player.getOutputStream());
                Integer playerID = server.playersId.get(server.playersSocket.indexOf(player));
                ServerMessage serverMessage = new ServerMessage("New player", server.maze.go(null, playerID), server.playerMoveNow.equals(player),
                        null, null, null, null,
                        positionItem(server.maze.go(null, playerID).getItems(), "Key"),
                        positionItem(server.maze.go(null, playerID).getItems(), "Gun"));
                writeToByteArray(os, serverMessage);
            }
        } catch (IOException e) {
            logger.error("Player disconnected (" + disconnect.getInetAddress() + ":" + disconnect.getPort() + ")");
        }
        logger.info("All players is notified");
    }

    private void notifyAboutMovePlayers(Direction direction, Position startPosition, Position finishPosition) {
        logger.info("Notify all players about move player");
        Socket disconnect = null;
        try {
            for (Socket player : server.playersSocket) {
                disconnect = player;
                DataOutputStream os = new DataOutputStream(player.getOutputStream());
                Integer playerID = server.playersId.get(server.playersSocket.indexOf(player));
                Player playerInMaze = null;
                for (Player pl : server.maze.getPlayers()) {
                    if (pl.getId() == idPlayer) playerInMaze = pl;
                }
                ServerMessage serverMessage = new ServerMessage(startPosition.getX() == finishPosition.getX() && startPosition.getY() == finishPosition.getY() ? null : "Move",
                        server.maze.go(null, playerID),
                        server.playerMoveNow.equals(player),
                        playerInMaze,
                        direction,
                        startPosition, finishPosition,
                        positionItem(server.maze.go(null, playerID).getItems(), "Key"),
                        positionItem(server.maze.go(null, playerID).getItems(), "Gun"));
                writeToByteArray(os, serverMessage);
            }
        } catch (IOException e) {
            logger.error("Player disconnected (" + disconnect.getInetAddress() + ":" + disconnect.getPort() + ")");
        }
        logger.info("All players is notified");
    }

    private void notifyAboutShotPlayers(Direction direction, Position startPosition, Position finishPosition) {
        logger.info("Notify all players about shot player");
        Socket disconnect = null;
        try {
            for (Socket player : server.playersSocket) {
                disconnect = player;
                DataOutputStream os = new DataOutputStream(player.getOutputStream());
                Integer playerID = server.playersId.get(server.playersSocket.indexOf(player));
                Player playerInMaze = null;
                for (Player pl : server.maze.getPlayers()) {
                    if (pl.getId() == idPlayer) playerInMaze = pl;
                }
                ServerMessage serverMessage = new ServerMessage("Shot",
                        server.maze.go(null, playerID),
                        server.playerMoveNow.equals(player),
                        playerInMaze,
                        direction,
                        startPosition, finishPosition,
                        positionItem(server.maze.go(null, playerID).getItems(), "Key"),
                        positionItem(server.maze.go(null, playerID).getItems(), "Gun"));
                writeToByteArray(os, serverMessage);
            }
        } catch (IOException e) {
            logger.error("Player disconnected (" + disconnect.getInetAddress() + ":" + disconnect.getPort() + ")");
        }
        logger.info("All players is notified");
    }

    private void notifyAboutUpdateMaze() {
        logger.info("Notify all players about update maze");
        Socket disconnect = null;
        try {
            for (Socket player : server.playersSocket) {
                disconnect = player;
                DataOutputStream os = new DataOutputStream(player.getOutputStream());
                Integer playerID = server.playersId.get(server.playersSocket.indexOf(player));
                ServerMessage serverMessage = new ServerMessage("Resize", server.maze.go(null, playerID),
                        server.playerMoveNow.equals(player),
                        null, null, null, null,
                        positionItem(server.maze.go(null, playerID).getItems(), "Key"),
                        positionItem(server.maze.go(null, playerID).getItems(), "Gun"));
                writeToByteArray(os, serverMessage);
            }
        } catch (IOException e) {
            logger.error("Player disconnected (" + disconnect.getInetAddress() + ":" + disconnect.getPort() + ")");
        }
        logger.info("All players is notified");
    }

    private void nextPlayer() {
        logger.info("Determined by the next player which will turn");
        if (server.playersSocket.size() == 0) {
            logger.info("All players disconnected. Server shuts down");
            System.exit(0);
        }
        if (server.iteratorPlayers.hasNext()) {
            server.playerMoveNow = server.iteratorPlayers.next();
        } else {
            server.iteratorPlayers = server.playersSocket.iterator();
            server.playerMoveNow = server.iteratorPlayers.next();
        }
        logger.info("Player(" + client.getInetAddress().getHostAddress() + ":" + client.getPort() + ") turn right now");
    }

    //check contains item with that name in this list

    private Position positionItem(List<Item> items, String name) {
        for (Item i : items) {
            if (i.getName().equals(name)) {
                return i.getPosition();
            }
        }
        return null;
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