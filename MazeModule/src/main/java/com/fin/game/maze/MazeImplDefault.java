package com.fin.game.maze;

import com.fin.game.cover.Cover;
import com.fin.game.cover.Direction;
import com.fin.game.cover.Field;
import com.fin.game.player.Item;
import com.fin.game.player.Player;
import com.fin.game.player.Position;

import java.io.Serializable;
import java.util.*;

public class MazeImplDefault implements Maze, Serializable {
    private Cover cover;

    private List<Player> players;

    private List<Item> items;
    private int sizeCover;
    private int idCounter;

    public MazeImplDefault(Cover cover) {
        this(cover, new ArrayList<>());
        players.add(new Player(sizeCover - 1, sizeCover - 1, sizeCover, -1));
    }

    public MazeImplDefault(Cover cover, List<Player> players) {
        this(cover, players, new ArrayList<>());
        items.add(new Item("Key", sizeCover / 2, sizeCover / 2));
        items.add(new Item("Gun", sizeCover / 2 + 1, sizeCover / 2 + 1));
    }

    public MazeImplDefault(Cover cover, List<Player> players, List<Item> items) {
        this.cover = cover;
        sizeCover = (int) Math.sqrt(cover.getCov().size());
        this.players = players;
        this.items = items;
    }

    @Override
    public Maze start(int x, int y) {
        Player player = new Player(x, y, sizeCover, idCounter++);
        takeItem(player);
        players.add(player);
        return getVisibleMaze(this, player);
    }

    @Override
    public Maze go(Direction direction, int idPlayer) {
        Player player = null;
        for (Player p : players) {
            if (p.getId() == idPlayer) player = p;
        }
        takeItem(player);
        if (direction != null) {
            if (!cover.containsWall(player.getX(), player.getY(), direction)) {
                switch (direction) {
                    case UP:
                        if (player.getY() - 1 != -1 || player.getY() - 1 != sizeCover) player.setY(player.getY() - 1);
                        break;
                    case DOWN:
                        if (player.getY() + 1 != -1 || player.getY() + 1 != sizeCover) player.setY(player.getY() + 1);
                        break;
                    case LEFT:
                        if (player.getX() - 1 != -1 || player.getX() - 1 != sizeCover) player.setX(player.getX() - 1);
                        break;
                    case RIGHT:
                        if (player.getX() + 1 != -1 || player.getX() + 1 != sizeCover) player.setX(player.getX() + 1);
                        break;
                }
                player.getStepList().addWall(player.getX(), player.getY(), Direction.MIDDLE);
            } else {
                player.getStepList().addWall(player.getX(), player.getY(), direction);
            }
        }
        takeItem(player);
        Player monster = null;
        for (Player p : players) {
            if (p.getId() == -1) monster = p;
        }
        if (monster != null && player.getX() == monster.getX() && player.getY() == monster.getY()) {
            player.setX(0);
            player.setY(sizeCover - 1);
            player.remove("Key");
            player.remove("Gun");
            items.add(new Item("Key", sizeCover / 2, sizeCover / 2));
            items.add(new Item("Gun", sizeCover / 2 + 1, sizeCover / 2 + 1));
        }
        return getVisibleMaze(this, player);
    }

    @Override
    public void setCover(Cover cover) {
        this.cover = cover;
    }

    @Override
    public int getSize() {
        return sizeCover;
    }

    @Override
    public Position shot(int idPlayer, Direction direction) {
        Player player = null;
        for (Player p : players) {
            if (p.getId() == idPlayer) player = p;
        }
        if (player.contains("Gun")) {
            player.remove("Gun");
            items.add(new Item("Gun", sizeCover / 2 + 1, sizeCover / 2 + 1));
            int bulletX = player.getX();
            int bulletY = player.getY();
            while (!cover.containsWall(bulletX, bulletY, direction)) {
                switch (direction) {
                    case UP:
                        bulletY--;
                        break;
                    case DOWN:
                        bulletY++;
                        break;
                    case LEFT:
                        bulletX--;
                        break;
                    case RIGHT:
                        bulletX++;
                        break;
                }
                Iterator<Player> iterator = players.iterator();
                while (iterator.hasNext()) {
                    Player i = iterator.next();
                    if (i.getId() == -1 && i.getX() == bulletX && i.getY() == bulletY) {
                        iterator.remove();
                        int finalBulletX = bulletX;
                        int finalBulletY = bulletY;
                        return new Position() {

                            @Override
                            public int getX() {
                                return finalBulletX;
                            }

                            @Override
                            public void setX(int x) {

                            }

                            @Override
                            public int getY() {
                                return finalBulletY;
                            }

                            @Override
                            public void setY(int y) {

                            }
                        };
                    }
                }
            }
            int finalBulletX1 = bulletX;
            int finalBulletY1 = bulletY;
            return new Position() {
                @Override
                public int getX() {
                    return finalBulletX1;
                }

                @Override
                public void setX(int x) {

                }

                @Override
                public int getY() {
                    return finalBulletY1;
                }

                @Override
                public void setY(int y) {

                }
            };
        }
        return null;
    }

    @Override
    public void deletePlayer(int idPlayer) {
        Player player = null;
        for (Player p : players) {
            if (p.getId() == idPlayer) player = p;
        }
        players.remove(player);
    }

    @Override
    public Player getFirstPlayer() {
        return players.get(0);
    }

    @Override
    public List<Player> getPlayers() {
        return players;
    }

    @Override
    public Cover getCover() {
        return cover;
    }

    @Override
    public List<Item> getItems() {
        return items;
    }

    @Override
    public void addAllPlayer(List<Player> players) {
        this.players.addAll(players);
    }

    public static Maze generateMaze(int s) {
        size = s;
        maze = new int[s][s];
        generateMaze(0, 0);
        for (int i = 0; i < maze.length; i++) {
            for (int j = i + 1; j < maze.length; j++) {
                int temp = maze[i][j];
                maze[i][j] = maze[j][i];
                maze[j][i] = temp;
            }
        }

        Set<Field> cover = new HashSet<>();
        for (int i = 0; i < s; i++) {
            for (int j = 0; j < s; j++) {
                Field f = new Field(j, i);
                if ((maze[i][j] & 1) == 0) f.addWalls(Direction.UP);
                if (i == maze.length - 1 || (maze[i + 1][j] & 1) == 0) f.addWalls(Direction.DOWN);
                if ((maze[i][j] & 8) == 0) f.addWalls(Direction.LEFT);
                if (j == maze.length - 1 || (maze[i][j + 1] & 8) == 0) f.addWalls(Direction.RIGHT);
                cover.add(f);
            }
        }
        return new MazeImplDefault(new Cover(cover));
    }

    private void takeItem(Player player) {
        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            Item i = iterator.next();
            if (i.getX() == player.getX() && i.getY() == player.getY()) {
                player.add(i);
                iterator.remove();
            }
        }
    }

    private Maze getVisibleMaze(MazeImplDefault maze, Player player) {
        Cover visibleCover = getVisibleCover(maze, player);
        List<Player> visiblePlayers = getVisiblePlayers(maze, player);
        List<Item> visibleItems = getVisibleItem(maze, player);
        return new MazeImplDefault(visibleCover, visiblePlayers, visibleItems);
    }

    private List<Player> getVisiblePlayers(MazeImplDefault mazeImplDefault, Player player) {
        List<Player> players = new ArrayList<>();
        players.add(player);
        for (Player p : mazeImplDefault.players) {
            if (!p.equals(player) && player.getStepList().containsWall(p.getX(), p.getY(), Direction.MIDDLE))
                players.add(p);
        }
        return players;
    }

    private Cover getVisibleCover(MazeImplDefault mazeImplDefault, Player player) {
        Cover visibleCover = new Cover(sizeCover);
        for (Field field : mazeImplDefault.cover.getCov()) {
            for (Direction wall : Direction.values()) {
                if (field.containsWall(wall) && player.getStepList().containsWall(field.getX(), field.getY(), wall)) {
                    visibleCover.addWall(field.getX(), field.getY(), wall);
                }
            }
        }
        return visibleCover;
    }

    private List<Item> getVisibleItem(MazeImplDefault maze, Player player) {
        List<Item> it = new ArrayList<>();
        for (Item i : maze.items) {
            if (player.getStepList().containsWall(i.getX(), i.getY(), Direction.MIDDLE)) it.add(i);
        }
        return it;
    }


    private static int[][] maze;
    private static int size;

    private static void generateMaze(int cx, int cy) {
        DIR[] dirs = DIR.values();
        Collections.shuffle(Arrays.asList(dirs));
        for (DIR dir : dirs) {
            int nx = cx + dir.dx;
            int ny = cy + dir.dy;
            if (between(nx, size) && between(ny, size)
                    && (maze[nx][ny] == 0)) {
                maze[cx][cy] |= dir.bit;
                maze[nx][ny] |= dir.opposite.bit;
                generateMaze(nx, ny);
            }
        }
    }

    private static boolean between(int v, int upper) {
        return (v >= 0) && (v < upper);
    }

    private enum DIR {
        N(1, 0, -1), S(2, 0, 1), E(4, 1, 0), W(8, -1, 0);
        private final int bit;
        private final int dx;
        private final int dy;
        private DIR opposite;

        // use the static initializer to resolve forward references
        static {
            N.opposite = S;
            S.opposite = N;
            E.opposite = W;
            W.opposite = E;
        }

        private DIR(int bit, int dx, int dy) {
            this.bit = bit;
            this.dx = dx;
            this.dy = dy;
        }
    }
}
