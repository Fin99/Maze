package com.fin;

public class NotifierThread extends Thread {
    private final Server server;

    public NotifierThread(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (server) {
                try {
                    server.notify();
                    server.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
