package aoop.asteroids.model.packet;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Logger;

abstract public class GamePacket implements Serializable {
    transient private final static Logger logger = Logger.getLogger(GamePacket.class.getName());

    public boolean sendPacket(DatagramSocket from, InetAddress inetAddress, int port) {
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        ObjectOutput oo;
        try {
            oo = new ObjectOutputStream(bStream);
            oo.writeObject(this);
            oo.close();
        } catch (IOException e) {
            getLogger().severe("[ERROR] Packet sending error: " +  e.getMessage());
            e.printStackTrace();
            return false;
        }
        byte [] serializedMessage = bStream.toByteArray();
        try {
            DatagramPacket formattedPacket = new DatagramPacket(serializedMessage, serializedMessage.length, inetAddress, port);
            from.send(formattedPacket);
        } catch (IOException e) {
            getLogger().severe("[ERROR] Packet sending error: " +  e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Logger getLogger() {
        return logger;
    }
}

