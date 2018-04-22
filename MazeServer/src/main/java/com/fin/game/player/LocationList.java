package com.fin.game.player;

import java.util.ArrayList;

public class LocationList {
    ArrayList<Player> peoples;

    {
        peoples = new ArrayList<>();
    }

    public Character containsPerson(int x, int y) {
        for (Player person : peoples) {
            if (person.getPlayerX() == x && person.getPlayerY() == y) return person.getIcon();
        }
        return null;
    }

    public int addPerson(int x, int y, char icon, int size) {
        Player player = new Player(icon, size);
        player.setPlayerX(x);
        player.setPlayerY(y);
        int result = peoples.size();
        peoples.add(player);
        return result;
    }

    public Player getPersonByID(int id) {
        return peoples.get(id);
    }

    public void deleteBullet() {
        Player bullet = null;
        for (Player player : peoples) {
            if (player.getIcon().equals('-') || player.getIcon().equals('|')) bullet = player;
        }
        peoples.remove(bullet);
    }
}
