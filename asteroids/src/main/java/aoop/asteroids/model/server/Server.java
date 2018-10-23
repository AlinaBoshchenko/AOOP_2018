package aoop.asteroids.model.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class Server implements Observer, Runnable {
    private DatagramSocket datagramSocket;
    private final int port;
    private final int maxSpectators;
    private boolean running;
    private List<ConnectedClient> connectedClients;

    public Server(int port, int maxSpectators) {
        this.port = port;
        this.maxSpectators = maxSpectators;
        connectedClients = new ArrayList<>(maxSpectators);
    }

    @Override
    public void run() {
        running = false;
        try {
            datagramSocket = new DatagramSocket(port);
            running = true;
        } catch (SocketException e) {
            //TODO: Logger
            e.printStackTrace();
        } catch (SecurityException e) {
            //TODO: Logger
            e.printStackTrace();
        }
        try {
            System.out.println("DataGram socket created on ip: " + InetAddress.getLocalHost() + " on port: " + port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        byte bytes[] = new byte[1024];
        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
        while(running) {
            try {
                System.out.println("Waiting for packet");
                datagramSocket.receive(datagramPacket);
                System.out.println("Packet received");
                handlePacket(datagramPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void handlePacket(DatagramPacket datagramPacket) throws IOException, ClassNotFoundException {
        ObjectInputStream objStream = new ObjectInputStream(new ByteArrayInputStream(datagramPacket.getData()));
        PacketHeader packetHeader = (PacketHeader) objStream.readObject();
        switch (packetHeader) {
            default:
            case PACKET_LOGIN:
                clientLogin(datagramPacket.getAddress(), datagramPacket.getPort());
//            case PACKET_SPECTATE:
//                spectating(datagramPacket.getAddress();
//                break;
//            case PACKET_DISCONNECT:
//                disconnectAll();
//                break;
        }

    }


    /**
     * Add all clients to a server
     */
    private void clientLogin(InetAddress ip, int port) {
        if(connectedClients.size() != maxSpectators) {
            this.connectedClients.add(new ConnectedClient(ip));
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            ObjectOutput oo;
            try {
                oo = new ObjectOutputStream(bStream);
                oo.writeObject(PacketHeader.PACKET_ACCEPTED);
                oo.close();
            } catch (IOException e) {
                //TODO: logger
                e.printStackTrace();
                return;
            }
            byte [] serializedMessage = bStream.toByteArray();
            DatagramSocket datagramSocket;
            DatagramPacket datagramPacket;
            try {
                datagramSocket = new DatagramSocket();
                datagramPacket = new DatagramPacket(serializedMessage, serializedMessage.length, ip, port);
                datagramSocket.send(datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }



    }

    /**
     * Add all spectators to the server
     */
//    private void spectating(InetAddress ip) {
//
//        ConnectedClient spectator = new ConnectedClient(ip);
//        this.connectedClients().add(spectator);
//    }

    /**
     * disconnects all spectators and diagramSocket from the server
     * */
//    private void disconnectAll() {
//        for (ConnectedClient spectator : getSpectators()) {
//            this.getSpectators().remove(spectator);
//           // datagramSocket.disconnect();
//            datagramSocket.close();
//
//        }
//    }
//
//    private ArrayList<ConnectedClient> getSpectators() {
//        ArrayList<ConnectedClient> spectators;
//        spectators = (ArrayList<ConnectedClient>) this.connectedClients;
//        return spectators;
//    }




    @Override
    public void update(Observable o, Object arg) {

    }
}
