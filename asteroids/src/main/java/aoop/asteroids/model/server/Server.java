package aoop.asteroids.model.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
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

    Server(int port, int maxSpectators) {
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
        byte bytes[] = new byte[1024];
        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
        while(running) {
            try {
                datagramSocket.receive(datagramPacket);
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
            //case PACKET_LOGIN:
        }

    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
