package aoop.asteroids.model.server;

import aoop.asteroids.model.packet.ClientAskSpectatePacket;
import aoop.asteroids.model.packet.ClientSpectatingPacket;
import aoop.asteroids.model.packet.GamePacket;
import aoop.asteroids.model.packet.server.ServerSpectatingAcceptedPacket;
import aoop.asteroids.model.packet.server.ServerSpectatingDeniedPacket;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server implements Observer, Runnable {
    private DatagramSocket datagramSocket;
    private final int port;
    private final int maxSpectators;
    private boolean running;
    private final Set<ConnectedClient> connectedClients;

    public Server(int port, int maxSpectators) {
        this.port = port;
        this.maxSpectators = maxSpectators;
        connectedClients = Collections.synchronizedSet(new LinkedHashSet<>(maxSpectators));
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


    private void handlePacket(GamePacket packet, InetAddress inetAddress, int port) {
        if(packet instanceof ClientAskSpectatePacket) {
            handleAskSpectate(inetAddress, port);
        } else if (packet instanceof ClientSpectatingPacket) {
            handleSpectatingPacket(inetAddress, port);
        }
    }

    private void handleSpectatingPacket(InetAddress from, int port) {
        ConnectedClient client = new ConnectedClient(from, port);
        synchronized (connectedClients) {
            for (ConnectedClient iteratedClient : connectedClients) {
                if (iteratedClient.equals(client)) {
                    iteratedClient.refreshTimeoutTicks();
                }
            }
        }
    }

    private void handleAskSpectate(InetAddress from, int port) {
        if(connectedClients.size() == maxSpectators) {
            new ServerSpectatingDeniedPacket().sendEmptyPacket(datagramSocket, from, port);
            return;
        }
        new ServerSpectatingAcceptedPacket().sendEmptyPacket(datagramSocket, from, port);
        synchronized(connectedClients) {
            connectedClients.add(new ConnectedClient(from, port));
        }

    }




    @Override
    public void update(Observable o, Object arg) {
        synchronized (connectedClients) {
            Iterator<ConnectedClient> iterator = connectedClients.iterator();
            ConnectedClient client;
            while (iterator.hasNext()) {
                client = iterator.next();
                client.decreaseTimeoutTicks();
                if(client.isTimeouted()) {
                    iterator.remove();
                    System.out.println("Cleared client.");
                }
            }
        }
    }
}
