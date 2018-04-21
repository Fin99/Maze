package com.fin.game;

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
    public int addPerson(int x, int y, char icon){
        Player player = new Player(icon);
        player.checked = new int[MazeImpl.size][MazeImpl.size];
        for(int i=0; i<player.checked.length;i++){
            for(int j=0; j<player.checked.length;j++){
                player.checked[i][j]=1;
            }
        }
        player.setPlayerX(x);
        player.setPlayerY(y);
        int result = peoples.size();
        peoples.add(player);
        return result;
    }

    public int addPerson(int x, int y, char icon, int id){
        Player player = new Player(icon);
        player.checked = new int[MazeImpl.size][MazeImpl.size];
        for(int i=0; i<player.checked.length;i++){
            for(int j=0; j<player.checked.length;j++){
                player.checked[i][j]=1;
            }
        }
        player.setPlayerX(x);
        player.setPlayerY(y);
        peoples.add(id, player);
        return id;
    }
    public Player getPersonByID(int id){
        return peoples.get(id);
    }
    public void deleteBullet(){
        Player bullet = null;
        for (Player player : peoples){
            if(player.getIcon().equals('-') || player.getIcon().equals('|'))bullet = player;
        }
        peoples.remove(bullet);
    }
}
