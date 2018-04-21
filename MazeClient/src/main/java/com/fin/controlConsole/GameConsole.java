package com.fin.controlConsole;

import java.io.IOException;

public interface GameConsole {

    String readLine() throws IOException;

    void writeLine(String s) throws IOException;

    void game();
}
