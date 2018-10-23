package aoop.asteroids.model.client;

import aoop.asteroids.model.packet.ClientAskSpectatePacket;
import aoop.asteroids.model.packet.GamePacket;
import aoop.asteroids.model.packet.server.ServerSpectatingDeniedPacket;

import java.io.*;
import java.net.*;
import java.util.logging.Logger;

public class Client implements Runnable {
    private final static int CLIENT_TIMEOUT_TIME = 5;
    private InetAddress inetAddress;
    private int port;
    private boolean connected;
    private final static Logger logger = Logger.getLogger(Client.class.getName());
    private DatagramSocket datagramSocket;


    public Client(InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        this.port = port;
    }

    private boolean enstablishConnection(InetAddress inetAddress, int port) {
        try {
            datagramSocket = new DatagramSocket(44445);
        } catch (SocketException e) {
            logger.severe("[ERROR] Could not create the datagram socket: " + e.getMessage());
            return false;
        }
        if(!(new ClientAskSpectatePacket().sendEmptyPacket(datagramSocket, inetAddress, port))) {
            return false;
        }
        System.out.println("Sent packet to " + inetAddress.getHostAddress());
        try {
            datagramSocket.setSoTimeout(CLIENT_TIMEOUT_TIME*10000);
        } catch (SocketException e) {
            logger.severe("[ERROR] Could not set the timeout for socket: " + e.getMessage());
            return false;
        }
        byte [] bytes = new byte[1024];
        DatagramPacket responsePacket;
        while (true) {
            responsePacket = new DatagramPacket(bytes, bytes.length);
            try {
                datagramSocket.receive(responsePacket);
                ObjectInputStream objStream = new ObjectInputStream(new ByteArrayInputStream(responsePacket.getData()));
                GamePacket gamePacket = (GamePacket) objStream.readObject();
                return !(gamePacket instanceof ServerSpectatingDeniedPacket);
            } catch (IOException | ClassNotFoundException e) {
                logger.severe("[ERROR] Could not establish handshake with the server: " + e.getMessage());
                return false;
            }
        }
    }

    @Override
    public void run() {
        System.out.println("Trying connection with " + inetAddress.getHostAddress() + ":" +  port);
        connected = enstablishConnection(inetAddress, port);
        if(!connected) {
            datagramSocket.close();
            logger.severe("[ERROR] Could not open client datagram socket.");
        }
        System.out.println("HANDSHAKE");
    }
}
