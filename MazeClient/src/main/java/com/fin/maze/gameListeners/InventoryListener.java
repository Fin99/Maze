package com.fin.maze.gameListeners;

import com.fin.Listener;
import com.fin.maze.gameEvent.InventoryEvent;

public interface InventoryListener<T extends InventoryEvent> extends Listener {
    void handle(T inventoryEvent);
}
