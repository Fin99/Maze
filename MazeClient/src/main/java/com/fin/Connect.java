package com.fin;

import java.io.*;
import java.net.Socket;

public class Connect {

    private Socket server;
    private DataInputStream is;
    private DataOutputStream os;

    public Connect(Socket socket) {
        try {
            server = socket;
            os = new DataOutputStream(server.getOutputStream());
            is = new DataInputStream(server.getInputStream());
        } catch (IOException e) {
            System.err.println("Указан неверный порт или сервер недоступен.");
            System.exit(1);
        }

    }

    public void sendRequest(Serializable... serializables) {
        try {
            for (Serializable s : serializables) {
                writeToByteArray(os, s);
            }
        } catch (IOException e) {
            System.err.println("Разорвано соединение с сервером");
            System.exit(1);
        }
    }

    public Object waitResponse() {
        try {
            return readFromByteArray(is);
        } catch (IOException e) {
            System.err.println("Разорвано соединение с сервером");
            System.exit(1);
            return null;
        }
    }

    public void writeToByteArray(DataOutputStream stream, Object element) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(element);
        byte[] buffObj = baos.toByteArray();
        stream.writeInt(buffObj.length);
        stream.write(buffObj);
        stream.flush();
    }

    private Object readFromByteArray(DataInputStream stream) throws IOException {
        int lengthBuff = stream.readInt();
        byte[] buff = new byte[lengthBuff];
        stream.readFully(buff, 0, lengthBuff);
        ByteArrayInputStream bais = new ByteArrayInputStream(buff);
        ObjectInputStream in = new ObjectInputStream(bais);
        try {
            return in.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}

