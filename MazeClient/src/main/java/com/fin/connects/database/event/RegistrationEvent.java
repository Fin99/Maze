package com.fin.connects.database.event;

import com.fin.Event;
import com.fin.entity.User;

public class RegistrationEvent implements Event {
    private final User user;

    public RegistrationEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
