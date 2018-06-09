package com.fin.turn;

import com.fin.Listener;

public interface TurnListener<T extends TurnEvent> extends Listener {
    void handle(TurnEvent turnEvent);
}
