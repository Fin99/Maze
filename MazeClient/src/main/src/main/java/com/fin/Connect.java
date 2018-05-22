package com.fin;

import java.io.*;
import java.net.Socket;

public class Connect {

    private volatile static Socket server;
    private volatile static DataInputStream is;
    private volatile static DataOutputStream os;


    private Connect() {
    }

    public static void setServer(Socket s) {
        try {
            server = s;
            os = new DataOutputStream(server.getOutputStream());
            is = new DataInputStream(server.getInputStream());
            System.out.println("set new server");//todo
        } catch (IOException e) {
            System.err.println("Указан неверный порт или сервер недоступен.");
        }
    }

    public static void sendRequest(Serializable... serializables) {
        if(server==null)throw new NullPointerException("Server not initialize");
        try {
            for (Serializable s : serializables) {
                writeToByteArray(os, s);
            }
        } catch (IOException e) {
            System.err.println("Разорвано соединение с сервером при отправке сообщения");
        }
    }

    public static Object waitResponse() {
        if(server==null)throw new NullPointerException("Server not initialize");
        try {
            return readFromByteArray(is);
        } catch (IOException e) {
            System.err.println("Разорвано соединение с сервером при ожидании ответа");
            return null;
        }
    }

    public static void writeToByteArray(DataOutputStream stream, Object element) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(element);
        byte[] buffObj = baos.toByteArray();
        stream.writeInt(buffObj.length);
        stream.write(buffObj);
        stream.flush();
    }

    public static Object readFromByteArray(DataInputStream stream) throws IOException {
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

