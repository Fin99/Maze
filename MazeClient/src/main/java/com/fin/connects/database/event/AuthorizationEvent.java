package com.fin.connects.database.event;

import com.fin.Event;
import com.fin.entity.User;

public class AuthorizationEvent implements Event {
    private final User user;

    public AuthorizationEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
