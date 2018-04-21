package com.fin.game;

public class InvisibleMazeWithKey extends MazeWithKey implements OnlineMaze {
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
        monster = new Player('☻');
        monster.setPlayerX(size - 1);
        monster.setPlayerY(size - 1);
        keyX = size / 2;
        keyY = size / 2;
        gun = new Player('◄');
        gun.setPlayerX(keyX + 1);
        gun.setPlayerY(keyY - 1);
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
            if (dx != 0) {
                if (dx == 1) {
                    if (player.checked[player.getPlayerX()][player.getPlayerY()] % 7 != 0)
                        player.checked[player.getPlayerX()][player.getPlayerY()] *= 7;
                    if (player.checked[player.getPlayerX() + 1][player.getPlayerY()] % 2 != 0)
                        player.checked[player.getPlayerX() + 1][player.getPlayerY()] *= 2;
                    if ((maze[player.getPlayerX() + dx][player.getPlayerY()] & 8) == 8) {
                        player.setPlayerX(player.getPlayerX() + dx);
                        return true;
                    }
                } else {
                    if (player.checked[player.getPlayerX()][player.getPlayerY()] % 2 != 0)
                        player.checked[player.getPlayerX()][player.getPlayerY()] *= 2;
                    if (player.checked[player.getPlayerX() - 1][player.getPlayerY()] % 7 != 0)
                        player.checked[player.getPlayerX() - 1][player.getPlayerY()] *= 7;
                    if ((maze[player.getPlayerX()][player.getPlayerY()] & 8) == 8) {
                        player.setPlayerX(player.getPlayerX() + dx);
                        return true;
                    }
                }
            } else if (dy != 0) {
                if (dy == 1) {
                    if (player.checked[player.getPlayerX()][player.getPlayerY()] % 5 != 0)
                        player.checked[player.getPlayerX()][player.getPlayerY()] *= 5;
                    if (player.checked[player.getPlayerX()][player.getPlayerY() + 1] % 3 != 0)
                        player.checked[player.getPlayerX()][player.getPlayerY() + 1] *= 3;
                    //вниз | вверх
                    if ((maze[player.getPlayerX()][player.getPlayerY() + dy] & 1) == 1) {
                        player.setPlayerY(player.getPlayerY() + dy);
                        return true;
                    }
                } else {
                    if (player.checked[player.getPlayerX()][player.getPlayerY()] % 3 != 0)
                        player.checked[player.getPlayerX()][player.getPlayerY()] *= 3;
                    if (player.checked[player.getPlayerX()][player.getPlayerY() - 1] % 5 != 0)
                        player.checked[player.getPlayerX()][player.getPlayerY() - 1] *= 5;
                    if ((maze[player.getPlayerX()][player.getPlayerY()] & 1) == 1) {
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
        if (keyX == -1 && keyY == -1 && monster.getPlayerX() == -1 && monster.getPlayerY() == -1) {
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

        if (keyY == player.getPlayerY() && keyX == player.getPlayerX()) {
            keyY = -1;
            keyX = -1;
        }
        if (monster.getPlayerY() == player.getPlayerY() && monster.getPlayerX() == player.getPlayerX() && !player.getIcon().equals('-') && !player.getIcon().equals('|')) {
            player.setPlayerX(0);
            player.setPlayerY(size - 1);
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
                } else if (Math.floorMod(player.checked[j][i], 3) == 0) {
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
                    if (player.checked[j][i] % 2 == 0 || j == 0) {
                        out.append((maze[j][i] & 8) == 0 ? "| " + icon + " " : "  " + icon + " ");
                    } else {
                        out.append("  ").append(icon).append(" ");
                    }
                } else if (j == keyX && i == keyY) {
                    if (player.checked[j][i] % 2 == 0) {
                        out.append((maze[j][i] & 8) == 0 ? "| ¶ " : "  ¶ ");
                    } else if (player.checked[j][i] != 1) {
                        out.append("  ¶ ");
                    } else {
                        out.append("    ");
                    }
                } else if (j == monster.getPlayerX() && i == monster.getPlayerY()) {
                    if (player.checked[j][i] % 2 == 0) {
                        out.append((maze[j][i] & 8) == 0 ? "| " + monster.getIcon() + " " : "  " + monster.getIcon() + " ");
                    } else if (player.checked[j][i] != 1) {
                        out.append("  ").append(monster.getIcon()).append(" ");
                    } else {
                        out.append("    ");
                    }
                } else if (j == gun.getPlayerX() && i == gun.getPlayerY()) {
                    if (player.checked[j][i] % 2 == 0) {
                        out.append((maze[j][i] & 8) == 0 ? "| " + gun.getIcon() + " " : "  " + gun.getIcon() + " ");
                    } else if (player.checked[j][i] != 1) {
                        out.append("  ").append(gun.getIcon()).append(" ");
                    } else {
                        out.append("    ");
                    }
                } else if (j == 0) {
                    out.append("|   ");
                } else if (player.checked[j][i] % 2 == 0) {
                    out.append((maze[j][i] & 8) == 0 ? "|   " : "    ");
                } else {
                    out.append("    ");
                }
            }
            out.append("|");
            if (i == 0) out.append(" Ваш рюкзак:");
            if (i == 1 && player.isGun()) out.append(" Пистолет");
            if (i == 4 && keyY==-1 && keyX==-1) out.append(" Ключ найден!");
            out.append("\n");
        }
        // draw the bottom line
        for (int j = 0; j < size - 1; j++) {
            out.append("+---");
        }
        out.append("+");
        return out.toString();
    }

    @Override
    public int addPlayer(int x, int y, char icon) {
        return players.addPerson(x, y, icon);
    }

    public static void main(String[] args) {
        System.out.println("◄");
    }

    @Override
    public boolean shot(int idPlayer, Direction direction) {
        player = players.getPersonByID(idPlayer);
        if (player.isGun() && idBullet == 0) {
            player.setGun(false);
            idBullet = players.addPerson(player.getPlayerX(), player.getPlayerY(), direction.getIcon());
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
