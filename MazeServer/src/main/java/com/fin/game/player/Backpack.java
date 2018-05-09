package com.fin.game.player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Backpack implements Serializable {
    private List<Item> items;

    {
        items = new ArrayList<>();
    }

    public boolean contains(String string) {
        for(Item item : items){
            if(item.getName().equals(string))return true;
        }
        return false;
    }

    public void remove(String item) {
        items.removeIf(item1 -> item1.getName().equals(item));
    }

    public void add(Item item) {
        items.add(item);
    }
}
