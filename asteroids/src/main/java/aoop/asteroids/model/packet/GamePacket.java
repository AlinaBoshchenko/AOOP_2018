package aoop.asteroids.model.packet;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Logger;

abstract public class GamePacket implements Serializable {
    private byte [] contentAsByteArray;
    private final static Logger logger = Logger.getLogger(GamePacket.class.getName());


    public boolean sendEmptyPacket(DatagramSocket from, InetAddress inetAddress, int port) {
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        ObjectOutput oo;
        try {
            oo = new ObjectOutputStream(bStream);
            oo.writeObject(this);
            oo.close();
        } catch (IOException e) {
            getLogger().severe("[ERROR] Packet sending error: " +  e.getMessage());
            return false;
        }
        byte [] serializedMessage = bStream.toByteArray();
        try {
            DatagramPacket formatedPacket = new DatagramPacket(serializedMessage, serializedMessage.length, inetAddress, port);
            from.send(formatedPacket);
        } catch (IOException e) {
            getLogger().severe("[ERROR] Packet sending error: " +  e.getMessage());
            return false;
        }
        return true;
    }


    //protected abstract boolean sendPacket(DatagramSocket from, InetAddress inetAddress, int port);

    public static Logger getLogger() {
        return logger;
    }
}

