package com.fin.controlConsole;

import com.fin.connect.Connect;
import org.jline.terminal.Attributes;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

import java.io.*;

/**
 * Game console mode.
 * At each turn, the entire maze is overwritten over the old one.
 */
public class GameConsoleImpl implements GameConsole {
    private Terminal terminal;
    private Attributes defaultAttributes;
    private NonBlockingReader reader;
    private BufferedWriter writer;
    private Connect serverConnect;

    {
        terminal = TerminalBuilder.terminal();
        defaultAttributes = terminal.getAttributes();
        reader = terminal.reader();
        writer = new BufferedWriter(terminal.writer());
    }

    public GameConsoleImpl(Connect connect) throws IOException {
        serverConnect = connect;
    }

    @Override
    public String readLine() throws IOException {
        return Character.toString((char) reader.read());
    }

    @Override
    public void writeLine(String string) throws IOException {
        writer.write(string);
        writer.newLine();
        writer.flush();
    }

    @Override
    public void game() {
        //set the mode console to read one character
        setRawModeConsole();
        try {
            String response = (String) serverConnect.sendRequest();
            int cLines = response.split("\n").length;
            writeLine(response);
            Thread.sleep(200);
            String move;
            while (true) {
                if(reader.ready()){
                    move = readLine();
                } else {
                    move = "show";
                }
                response = (String) serverConnect.sendRequest(move);
                writer.write("\u001b["+ cLines+"F");
                writeLine(response);
                if(response.contains("Вы выиграли")) break;
                cLines = response.split("\n").length;
                Thread.sleep(200);
            }
        } catch (IOException e1) {
            System.err.println("Возникла ошибка при работа с терминалом.");
            System.exit(1);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }
    //set the mode console to read one character
    private void setRawModeConsole() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            terminal.setAttributes(defaultAttributes);
        }));
        terminal.enterRawMode();
    }
}
