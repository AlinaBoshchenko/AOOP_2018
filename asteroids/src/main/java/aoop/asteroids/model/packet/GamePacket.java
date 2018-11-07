package aoop.asteroids.model.packet;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Logger;

/**
 * Abstract class representing a game packet which is used as as the pivot of the UDP communication between server and client, and vice-versa.
 */
abstract public class GamePacket implements Serializable {
    transient private final static Logger logger = Logger.getLogger(GamePacket.class.getName());

    /**
     * Sends this packet from the indicated DatagramSocket to the specified address and port.
     */
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

    /**
     * Returns the logger for any packet.
     */
    public static Logger getLogger() {
        return logger;
    }
}

