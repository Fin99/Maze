package com.fin.maze.localEvent;

import java.util.Locale;

public class LocalEvent {
    private final Locale locale;

    public LocalEvent(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }
}
