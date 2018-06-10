package com.fin.game.player;

import com.fin.game.cover.Field;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Backpack)) return false;
        Backpack backpack = (Backpack) o;
        if (items.size() != backpack.items.size()) return false;
        Iterator<Item> covI = items.iterator();
        Iterator<Item> coverI = backpack.items.iterator();
        while (covI.hasNext()) {
            if (!covI.next().equals(coverI.next())) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {

        return Objects.hash(items);
    }
}
