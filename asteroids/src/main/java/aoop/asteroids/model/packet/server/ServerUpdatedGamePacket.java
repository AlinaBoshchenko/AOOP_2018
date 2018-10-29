package aoop.asteroids.model.packet.server;

import aoop.asteroids.model.Game;
import aoop.asteroids.model.client.Client;

import java.net.InetAddress;

public class ServerUpdatedGamePacket extends ServerGamePacket {
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
