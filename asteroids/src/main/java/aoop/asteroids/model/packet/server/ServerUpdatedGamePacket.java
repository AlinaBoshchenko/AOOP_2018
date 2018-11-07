package aoop.asteroids.model.packet.server;

import aoop.asteroids.model.Game;
import aoop.asteroids.model.client.Client;

import java.net.InetAddress;

/**
 * This class represents a packet a server would send to all it's clients (both spectator and players) to notify them about the most current
 * model of the game, which they can subsequently reproduce in their views.
 */
public class ServerUpdatedGamePacket extends ServerGamePacket {
    /**
     * The new game model which is sent with this packet.
     */
    private Game newGame;

    public  ServerUpdatedGamePacket(Game newGame) {
        this.newGame = newGame;
    }

    public Game getNewGame() {
        return newGame;
    }

    /**
     * This method describes the actions the specified client that received this packet should do.
     *
     * @param client        The client that received the packet
     * @param serverAddress The address of the server that sent the packet
     * @param serverPort    The port of the server that sent the packet
     */
    @Override
    public void handleServerPacket(Client client, InetAddress serverAddress, int serverPort) {
        System.out.println(getClass().getName());
    }
}
