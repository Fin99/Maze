package com.fin.connect;

import java.io.*;
import java.net.Socket;

public class ConnectImpl implements Connect {

    private Socket server;
    private DataInputStream is;
    private DataOutputStream os;

    public ConnectImpl(int portServer) {
        try {
            server = new Socket("localhost", portServer);
            os = new DataOutputStream(server.getOutputStream());
            is = new DataInputStream(server.getInputStream());
        } catch (IOException e) {
            System.err.println("Указан неверный порт или сервер недоступен.");
            System.exit(1);
        }

    }

    @Override
    public Object sendRequest(Serializable... serializables) {
        try {
            for (Serializable s : serializables) {
                os.write(writeToByteArray(s));
            }
            while (true) {
                if (is.available() > 0) {
                    byte[] response = new byte[is.available()];
                    is.read(response);
                    return readFromByteArray(response);
                }
            }
        } catch (IOException e) {
            System.err.println("Разорвано соединение с сервером");
            System.exit(1);
            return null;
        }
    }

    private byte[] writeToByteArray(Object element) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeObject(element);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error in conversion in byte array");
        }
    }

    private Object readFromByteArray(byte[] bytes) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream in = new ObjectInputStream(bais);
            return in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error in conversion in object");
        }
    }
}
