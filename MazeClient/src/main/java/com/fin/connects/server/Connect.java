package com.fin.connects.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;

public class Connect {
    //logger
    private final Logger logger = LogManager.getRootLogger();
    //
    private Socket server;
    private DataInputStream is;
    private DataOutputStream os;

    public Connect(Socket server) throws IOException {
        logger.info("Create new Connect");
        this.server = server;
        try {
            is = new DataInputStream(server.getInputStream());
            os = new DataOutputStream(server.getOutputStream());
        } catch (IOException ioe) {
            logger.error("Server("+server.getInetAddress().getHostName()+":"+server.getPort()+") isn`t available");
            throw new IOException("Server isn`t available", ioe);
        }
    }

    public void sendRequest(Serializable... serializables) {
        logger.info("Sending message is started");
        if (server == null) throw new NullPointerException("Server not initialize");
        try {
            for (Serializable s : serializables) {
                writeToByteArray(os, s);
            }
        } catch (IOException e) {
            logger.error("Connection to the server was lost when sending a message");
        }
        logger.info("Sending message is finished");
    }

    public Object waitResponse() {
        logger.info("Waiting message is started");
        if (server == null) throw new NullPointerException("Server not initialize");
        try {
            Object result = readFromByteArray(is);
            logger.info("There is new message");
            return result;
        } catch (IOException e) {
            logger.error("Connection to the server was lost while waiting for a response");
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

    public Object readFromByteArray(DataInputStream stream) throws IOException {
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
