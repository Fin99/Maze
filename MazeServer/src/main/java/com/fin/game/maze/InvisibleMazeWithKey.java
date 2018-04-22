package com.fin.game.maze;

import com.fin.game.player.Direction;
import com.fin.game.player.LocationList;
import com.fin.game.player.Player;
import com.fin.game.cover.Wall;

public class InvisibleMazeWithKey extends MazeImpl implements OnlineMaze {
    // 1 нет
    // 2 влево
    // 3 вверх
    // 5 вниз
    // 7 вправо
    private LocationList players;
    private Player player;
    private int idBullet;
    private Player monster;
    private Player gun;
    private Player key;

    public InvisibleMazeWithKey() {
        players = new LocationList();
    }

    @Override
    public void setMaze(int[][] maze) {
        super.setMaze(maze);

    }

    @Override
    public void setSize(int size) {
        super.setSize(size);
        monster = new Player('☻', size);
        monster.setPlayerX(size - 1);
        monster.setPlayerY(size - 1);
        key = new Player('¶', size);
        key.setPlayerX(size / 2);
        key.setPlayerY(size / 2);
        gun = new Player('◄', size);
        gun.setPlayerX(key.getPlayerX() + 1);
        gun.setPlayerY(key.getPlayerY() - 1);
    }

    @Override
    public String start(int idPlayer) {
        player = players.getPersonByID(idPlayer);
        return super.start();
    }

    @Override
    public String show(String s, int idPlayer) {
        player = players.getPersonByID(idPlayer);
        personX = player.getPlayerX();
        personY = player.getPlayerY();
        return display().trim() + "\n" + s + "                                                         ";
    }

    @Override
    public boolean canIGo(int dx, int dy) {
        if (player.getPlayerX() + dx < size && player.getPlayerX() + dx >= 0 && player.getPlayerY() + dy < size && player.getPlayerY() + dy >= 0) {
            player.getCover().addWall(player.getPlayerX(), player.getPlayerY(), Wall.MIDDLE);
            if (dx != 0) {
                if (dx == 1) {
                    player.getCover().addWall(player.getPlayerX(), player.getPlayerY(), Wall.RIGHT);
                    player.getCover().addWall(player.getPlayerX() + 1, player.getPlayerY(), Wall.LEFT);
                    if ((maze[player.getPlayerX() + dx][player.getPlayerY()] & 8) == 8) {
                        player.getCover().addWall(player.getPlayerX() + 1, player.getPlayerY(), Wall.MIDDLE);
                        player.setPlayerX(player.getPlayerX() + dx);
                        return true;
                    }
                } else {
                    player.getCover().addWall(player.getPlayerX(), player.getPlayerY(), Wall.LEFT);
                    player.getCover().addWall(player.getPlayerX() - 1, player.getPlayerY(), Wall.RIGHT);
                    if ((maze[player.getPlayerX()][player.getPlayerY()] & 8) == 8) {
                        player.getCover().addWall(player.getPlayerX() - 1, player.getPlayerY(), Wall.MIDDLE);
                        player.setPlayerX(player.getPlayerX() + dx);
                        return true;
                    }
                }
            } else if (dy != 0) {
                if (dy == 1) {
                    player.getCover().addWall(player.getPlayerX(), player.getPlayerY(), Wall.DOWN);
                    player.getCover().addWall(player.getPlayerX(), player.getPlayerY() + 1, Wall.UP);
                    if ((maze[player.getPlayerX()][player.getPlayerY() + dy] & 1) == 1) {
                        player.getCover().addWall(player.getPlayerX(), player.getPlayerY() + 1, Wall.MIDDLE);
                        player.setPlayerY(player.getPlayerY() + dy);
                        return true;
                    }
                } else {
                    player.getCover().addWall(player.getPlayerX(), player.getPlayerY(), Wall.UP);
                    player.getCover().addWall(player.getPlayerX(), player.getPlayerY() - 1, Wall.DOWN);
                    if ((maze[player.getPlayerX()][player.getPlayerY()] & 1) == 1) {
                        player.getCover().addWall(player.getPlayerX(), player.getPlayerY() - 1, Wall.MIDDLE);
                        player.setPlayerY(player.getPlayerY() + dy);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String go(String a, int idPlayer) {
        player = players.getPersonByID(idPlayer);
        StringBuilder out = new StringBuilder(super.go(a));
        if (player.isKey() && monster.getPlayerX() == -1 && monster.getPlayerY() == -1 && player.getPlayerX() == size - 1 && player.getPlayerY() == size - 1) {
            String[] elements = out.toString().split("\n");
            elements[out.toString().split("\n").length - 1] = "Вы выиграли";
            out = new StringBuilder();
            for (int i = 0; i < elements.length; i++) {
                out.append(elements[i]);
                if (i != elements.length - 1) out.append("\n");
            }
        }
        return out.toString();
    }

    @Override
    String display() {

        if (key.getPlayerY() == player.getPlayerY() && key.getPlayerX() == player.getPlayerX()) {
            key.setPlayerY(-1);
            key.setPlayerX(-1);
            player.setKey(true);
        }
        if (monster.getPlayerY() == player.getPlayerY() && monster.getPlayerX() == player.getPlayerX() && !player.getIcon().equals('-') && !player.getIcon().equals('|')) {
            player.setPlayerX(0);
            player.setPlayerY(size - 1);
            if (player.isKey()) {
                player.setKey(false);
                key.setPlayerX(size / 2);
                key.setPlayerY(size / 2);
            }
            if (player.isGun()) {
                player.setGun(false);
                gun.setPlayerX(size / 2 + 1);
                gun.setPlayerY(size / 2 + 1);
            }
        }
        if (gun.getPlayerY() == player.getPlayerY() && gun.getPlayerX() == player.getPlayerX()) {
            gun.setPlayerX(-1);
            gun.setPlayerY(-1);
            player.setGun(true);
        }

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == 0) {
                    out.append("+---");
                } else if (player.getCover().containsWall(j, i, Wall.UP)) {
                    out.append((maze[j][i] & 1) == 0 ? "----" : "    ");
                } else if (j == 0) {
                    out.append("    ");
                } else {
                    out.append("    ");
                }

            }
            if (i == 0) out.append("+");
            out.append("\n");
            for (int j = 0; j < size; j++) {
                Character icon;
                if ((icon = players.containsPerson(j, i)) != null) {
                    if (player.getCover().containsWall(j, i, Wall.LEFT) || j == 0) {
                        out.append((maze[j][i] & 8) == 0 ? "| " + icon + " " : "  " + icon + " ");
                    } else {
                        out.append("  ").append(icon).append(" ");
                    }
                } else if (j == key.getPlayerX() && i == key.getPlayerY()) {
                    out.append(drawPlayer(player, key, j, i));
                } else if (j == monster.getPlayerX() && i == monster.getPlayerY()) {
                    out.append(drawPlayer(player, monster, j, i));
                } else if (j == gun.getPlayerX() && i == gun.getPlayerY()) {
                    out.append(drawPlayer(player, gun, j, i));
                } else if (j == 0) {
                    out.append("|   ");
                } else if (player.getCover().containsWall(j, i, Wall.LEFT)) {
                    out.append((maze[j][i] & 8) == 0 ? "|   " : "    ");
                } else {
                    out.append("    ");
                }
            }
            out.append("|");
            if (i == 0) out.append(" Ваш рюкзак:");
            if (i == 1) {
                if (player.isGun()) {
                    out.append(" Пистолет");
                } else {
                    out.append("         ");
                }
            }
            if (i == 2) {
                if (player.isKey()) {
                    out.append(" Ключ");
                } else {
                    out.append("     ");
                }
            }
            out.append("\n");
        }
        // draw the bottom line
        for (int j = 0; j < size - 1; j++) {
            out.append("+---");
        }
        out.append("+");
        return out.toString();
    }

    private String drawPlayer(Player player, Player subject, int j, int i) {
        StringBuilder out = new StringBuilder();
        if (player.getCover().containsWall(j, i, Wall.MIDDLE) && player.getCover().containsWall(j, i, Wall.LEFT)) {
            out.append((maze[j][i] & 8) == 0 ? "| " + subject.getIcon() + " " : "  " + subject.getIcon() + " ");
        } else if (player.getCover().containsWall(j, i, Wall.LEFT)) {
            out.append((maze[j][i] & 8) == 0 ? "|   " : "    ");
        } else if (player.getCover().containsWall(j, i, Wall.MIDDLE)) {
            out.append("  ").append(subject.getIcon()).append(" ");
        } else {
            out.append("    ");
        }
        return out.toString();
    }

    @Override
    public int addPlayer(int x, int y, char icon) {
        return players.addPerson(x, y, icon, size);
    }

    @Override
    public boolean shot(int idPlayer, Direction direction) {
        player = players.getPersonByID(idPlayer);
        if (player.isGun() && idBullet == 0) {
            player.setGun(false);
            idBullet = players.addPerson(player.getPlayerX(), player.getPlayerY(), direction.getIcon(), size);
            Player bullet = players.getPersonByID(idBullet);
            new Thread(() -> {
                boolean stop = false;
                do {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (bullet.getPlayerX() == monster.getPlayerX() && bullet.getPlayerY() == monster.getPlayerY()) {
                        monster.setPlayerX(-1);
                        monster.setPlayerY(-1);
                        stop = true;
                    }
                }
                while (!go(direction.getDir(), idBullet).contains("Неудачный ход") && !stop);
                players.deleteBullet();
                idBullet = 0;
            }).start();
            return true;
        }
        return false;
    }
}