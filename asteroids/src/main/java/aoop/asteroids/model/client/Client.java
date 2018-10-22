package aoop.asteroids.model.client;

import aoop.asteroids.model.server.PacketHeader;

import java.io.*;
import java.net.*;

public class Client {
    private final static int CLIENT_TIMEOUT_TIME = 5;
    private InetAddress inetAddress;
    private int port;
    private boolean connected;


    public Client(InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        this.port = port;
        connected = enstablishConnection(inetAddress, port);
        if(connected) {
            System.out.println("Rabotaet");
        } else {
            System.out.println("Pizdet.");
        }
    }

    private boolean enstablishConnection(InetAddress inetAddress, int port) {
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        ObjectOutput oo;
        try {
            oo = new ObjectOutputStream(bStream);
            oo.writeObject(PacketHeader.PACKET_LOGIN);
            oo.close();
        } catch (IOException e) {
            //TODO: logger
            e.printStackTrace();
            return false;
        }
        byte [] serializedMessage = bStream.toByteArray();
        DatagramSocket datagramSocket;
        DatagramPacket datagramPacket;
        try {
            datagramSocket = new DatagramSocket();
            datagramPacket = new DatagramPacket(serializedMessage, serializedMessage.length, inetAddress, port);
            datagramSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        try {
            datagramSocket.setSoTimeout(CLIENT_TIMEOUT_TIME*1000);
        } catch (SocketException e) {
            e.printStackTrace();
            return false;
        }
        byte [] bytes = new byte[1024];
        DatagramPacket responsePacket;
        while (true) {
            responsePacket = new DatagramPacket(bytes, bytes.length);
            try {
                datagramSocket.receive(responsePacket);
                ObjectInputStream objStream = new ObjectInputStream(new ByteArrayInputStream(datagramPacket.getData()));
                PacketHeader packetHeader = (PacketHeader) objStream.readObject();
                return !packetHeader.equals(PacketHeader.PACKET_DENIED);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

}
