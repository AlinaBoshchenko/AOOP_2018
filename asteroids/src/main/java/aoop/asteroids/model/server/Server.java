package aoop.asteroids.model.server;

import aoop.asteroids.model.Game;
import aoop.asteroids.model.client.Client;
import aoop.asteroids.model.packet.client.ClientGamePacket;
import aoop.asteroids.model.packet.server.ServerUpdatedGamePacket;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;

public class Server implements Observer, Runnable {

    /**
     * The DataGram socket that manages the UDP connections of the server.
     */
    private DatagramSocket datagramSocket;

    /**
     * The port on which the Server operates.
     */
    private final int port;

    /**
     * The maximum numbers of Spectators that can be connected to the server at the same time.
     */
    private final int maxSpectators;

    /**
     * The status of the server
     */
    private boolean running;

    /**
     * The set of all connected clients.
     */
    private final Set<ConnectedClient> connectedClients;

    /**
     * The most recent model of the game bound to the server.
      */
    private Game currentGame;

    /**
     * The logger of this class
     */
    private final static Logger logger = Logger.getLogger(Client.class.getName());


    /**
     * Creates a new server bound to the currentGame.
     * @param currentGame the game to which the server is bound
     * @param port the port on which the server will be created
     * @param maxSpectators the maximum number of spectators that can watch the game
     */
    public Server(Game currentGame, int port, int maxSpectators) {
        this.currentGame = currentGame;
        this.port = port;
        this.maxSpectators = maxSpectators;
        connectedClients = Collections.synchronizedSet(new LinkedHashSet<>(maxSpectators));
    }

    private void openDatagramSocket() {
        try {
            datagramSocket = new DatagramSocket(port);
            running = true;
        } catch (SocketException | SecurityException e) {
            logger.severe("Failed to open server Datagram socket: " + e.getMessage());
            running = false;
        }
    }

    @Override
    public void run() {
        running = false;
        openDatagramSocket();
        byte bytes[] = new byte[1024];
        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
        while(running) {
            try {
                datagramSocket.receive(datagramPacket);
                ObjectInputStream objStream = new ObjectInputStream(new ByteArrayInputStream(datagramPacket.getData()));
                Object receivedObject = objStream.readObject();
                objStream.close();
                if(!(receivedObject instanceof ClientGamePacket)) {
                    logger.severe("[SERVER] Received invalid packet from " + datagramPacket.getAddress().getHostAddress() + ":" + datagramPacket.getPort() + ".");
                    continue;
                }
                ((ClientGamePacket) receivedObject).handleClientPacket(this, datagramPacket.getAddress(), datagramPacket.getPort());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Set<ConnectedClient> getConnectedClients() {
        return connectedClients;
    }

    public int getMaxSpectators() {
        return maxSpectators;
    }

    public static Logger getLogger() {
        return logger;
    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    @Override
    public void update(Observable o, Object arg) {
        if(!(o instanceof Game)) {
            return;
        }
        currentGame = (Game) o;
        synchronized (connectedClients) {
            Iterator<ConnectedClient> iterator = connectedClients.iterator();
            ConnectedClient client;
            while (iterator.hasNext()) {
                client = iterator.next();
                client.decreaseTimeoutTicks();
                if(client.isTimeouted()) {
                    iterator.remove();
                    logger.fine("[SERVER] " + client.getInetAddress().getHostAddress() + ":" + client.getPort() + " disconnected.");
                    continue;
                }
                new ServerUpdatedGamePacket(currentGame).sendPacket(datagramSocket, client.getInetAddress(), client.getPort());
            }
        }
    }
}
