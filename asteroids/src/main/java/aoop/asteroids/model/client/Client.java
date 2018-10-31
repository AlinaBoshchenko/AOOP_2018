package aoop.asteroids.model.client;

import aoop.asteroids.gui.AsteroidsFrame;
import aoop.asteroids.model.Game;
import aoop.asteroids.model.packet.client.ClientAskSpectatePacket;
import aoop.asteroids.model.packet.client.ClientSpectatingPacket;
import aoop.asteroids.model.packet.GamePacket;
import aoop.asteroids.model.packet.client.ClientSpectatorDisconnectPacket;
import aoop.asteroids.model.packet.server.ServerGamePacket;
import aoop.asteroids.model.packet.server.ServerUpdatedGamePacket;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class Client extends Observable implements Runnable {
    private final static int CLIENT_TIMEOUT_TIME = 5;
    InetAddress serverAddress;
    int serverPort;
    AtomicBoolean connected = new AtomicBoolean(false);
    final static Logger logger = Logger.getLogger(Client.class.getName());
    DatagramSocket datagramSocket;
    private Game currentGame;

    public Client(InetAddress serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    private boolean establishConnection(InetAddress inetAddress, int port) {
        try {
            datagramSocket = new DatagramSocket(44445);
        } catch (SocketException e) {
            logger.severe("[ERROR] Could not create the datagram socket: " + e.getMessage());
            datagramSocket.close();
            return false;
        }
        if(!sendRequestPacket(inetAddress, port)) {
            datagramSocket.close();
            return false;
        }
        System.out.println("Sent packet to " + inetAddress.getHostAddress());
        try {
            datagramSocket.setSoTimeout(CLIENT_TIMEOUT_TIME*1000);
        } catch (SocketException e) {
            logger.severe("[ERROR] Could not set the timeout for socket: " + e.getMessage());
            datagramSocket.close();
            return false;
        }
        byte [] bytes = new byte[4096];
        DatagramPacket responsePacket;
        responsePacket = new DatagramPacket(bytes, bytes.length);
        try {
            datagramSocket.receive(responsePacket);
            ObjectInputStream objStream = new ObjectInputStream(new ByteArrayInputStream(responsePacket.getData()));
            GamePacket gamePacket = (GamePacket) objStream.readObject();
            objStream.close();
            if(gamePacket instanceof ServerUpdatedGamePacket) {
                createClientView((ServerUpdatedGamePacket) gamePacket);
                return true;
            } else {
                ((ServerGamePacket) gamePacket).handleServerPacket(this, responsePacket.getAddress(), responsePacket.getPort());
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.severe("[ERROR] Could not establish handshake with the server: " + e.getMessage());
            return false;
        }
        return false;
    }

    boolean sendRequestPacket(InetAddress inetAddress, int port) {
        return new ClientAskSpectatePacket().sendPacket(datagramSocket, inetAddress, port);
    }

    /**
     * Creates the client GUI after successful handshake with the server.
     * @param gamePacket the game packet containing the game information.
     */
    void createClientView(ServerUpdatedGamePacket gamePacket) {
        AsteroidsFrame frame = new AsteroidsFrame(gamePacket.getNewGame(), this);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new ClientSpectatorDisconnectPacket().sendPacket(datagramSocket, serverAddress, serverPort);
                System.exit(0);
            }
        });
    }

    /**
     * Creates and runs a new thread that sends the server the required information to maintain the connection.
     * In this case it sends an empty packet that notifies it that the client is still spectating.
     */
    void startClientResponseThread() {
        new Thread(() -> {
            while(connected.get()) {
                new ClientSpectatingPacket().sendPacket(datagramSocket, serverAddress, serverPort);
                try {
                    Thread.sleep(Game.getGameTickTime()*4);
                } catch (InterruptedException e) {
                    logger.severe("[ERROR] Spectating connection maintaining thread interrupted: " + e.getMessage());
                }
            }
        }).start();
    }


    @Override
    public void run() {
        System.out.println("Trying connection with " + serverAddress.getHostAddress() + ":" + serverPort);
        connected.set(establishConnection(serverAddress, serverPort));
        if(!connected.get()) {
            datagramSocket.close();
            logger.severe("[ERROR] Could not open client datagram socket.");
            return;
        }
        logger.fine("[TRACING] Client managed to establish handshake with the server.");
        startClientResponseThread();
        byte [] bytes = new byte[4096];
        DatagramPacket responsePacket;
        while(connected.get()) {
            responsePacket = new DatagramPacket(bytes, bytes.length);
            try {
                datagramSocket.receive(responsePacket);
                ObjectInputStream objStream = new ObjectInputStream(new ByteArrayInputStream(responsePacket.getData()));
                GamePacket gamePacket = (GamePacket) objStream.readObject();
                objStream.close();
                if(gamePacket instanceof ServerUpdatedGamePacket) {
                    Game newGame = ((ServerUpdatedGamePacket) gamePacket).getNewGame();
                    if(currentGame != null && !newGame.isNewer(currentGame)) {
                        continue;
                    }
                    this.currentGame = newGame;
                    setChanged();
                    notifyObservers();
                } else {
                    logger.severe("Received invalid packet type.");
                }
            } catch (IOException | ClassNotFoundException e) {
                logger.severe("[ERROR] Could not translate packet: " + e.getMessage());
            }


        }
    }

    public Game getCurrentGame() {
        return currentGame;
    }
}
