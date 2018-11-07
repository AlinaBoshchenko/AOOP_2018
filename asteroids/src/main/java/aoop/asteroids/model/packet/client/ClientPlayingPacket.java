package aoop.asteroids.model.packet.client;

import aoop.asteroids.model.MultiplayerGame;
import aoop.asteroids.model.Spaceship;
import aoop.asteroids.model.server.ConnectedClient;
import aoop.asteroids.model.server.MultiplayerServer;
import aoop.asteroids.model.server.Server;

import java.net.InetAddress;
import java.util.Set;

/**
 * This class represents a packet a player client would sent to a server in order to update it's state.
 */
public class ClientPlayingPacket extends ClientGamePacket {
    /**
     * The abstract object ship that contains the needed information about the ship of the client
     * that has sent the given packet.
     */
    private Spaceship abstractShip;

    public ClientPlayingPacket(Spaceship abstractShip) {
        this.abstractShip = abstractShip;
    }
    /**
     * This method describes the actions the specified server that received this packet should do.
     *
     * @param server        The server that received the packet
     * @param clientAddress The address of the client that sent the packet
     * @param clientPort    The port of the client that sent the packet
     */
    @Override
    public void handleClientPacket(Server server, InetAddress clientAddress, int clientPort) {
        if(!(server instanceof MultiplayerServer)) {
            Server.getLogger().severe("[SERVER] Multiplayer packet received on a non-multiplayer server.");
            return;
        }
        MultiplayerGame game = (MultiplayerGame) (server.getGame());
        ConnectedClient client = new ConnectedClient(clientAddress, clientPort);
        game.updateShipCoordinates(client, abstractShip);
        MultiplayerServer mpServer = (MultiplayerServer) server;
        synchronized (mpServer.getConnectedPlayers()) {
            Set<ConnectedClient> serverConnectedClients = mpServer.getConnectedPlayers();
            for (ConnectedClient iteratedClient : serverConnectedClients) {
                if (iteratedClient.equals(client)) {
                    iteratedClient.refreshTimeoutTicks();
                }
            }
        }
    }
}
