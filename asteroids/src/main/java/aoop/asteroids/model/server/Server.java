package aoop.asteroids.model.server;

import aoop.asteroids.model.packet.ClientAskSpectatePacket;
import aoop.asteroids.model.packet.GamePacket;
import aoop.asteroids.model.packet.server.ServerSpectatingAcceptedPacket;

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
            //System.out.println("Started server datagram on " + port);
            datagramSocket = new DatagramSocket(port);
            running = true;
        } catch (SocketException e) {
            //TODO: Logger
            e.printStackTrace();
        } catch (SecurityException e) {
            //TODO: Logger
            e.printStackTrace();
        }

        byte bytes[] = new byte[1024];
        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
        while(running) {
            try {
                datagramSocket.receive(datagramPacket);
                ObjectInputStream objStream = new ObjectInputStream(new ByteArrayInputStream(datagramPacket.getData()));
                handlePacket((GamePacket) objStream.readObject(), datagramPacket.getAddress(), datagramPacket.getPort());
                objStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    void handlePacket(GamePacket packet, InetAddress inetAddress, int port) {
        if(packet instanceof ClientAskSpectatePacket) {
            new ServerSpectatingAcceptedPacket().sendEmptyPacket(datagramSocket, inetAddress, port);
        }
    }




    @Override
    public void update(Observable o, Object arg) {

    }
}
