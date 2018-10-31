package aoop.asteroids.model.packet.client;

import aoop.asteroids.model.MultiplayerGame;
import aoop.asteroids.model.server.ConnectedClient;
import aoop.asteroids.model.server.MultiplayerServer;
import aoop.asteroids.model.server.Server;

import java.net.InetAddress;
import java.util.Set;

public class ClientPlayerDisconnectPacket extends ClientGamePacket {
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
            Server.getLogger().fine("[SERVER] Received invalid packet from " + clientAddress.getHostAddress() + ":" + clientPort);
            return;
        }
        ConnectedClient client = new ConnectedClient(clientAddress, clientPort);
        MultiplayerServer mpServer = (MultiplayerServer) server;
        synchronized (mpServer.getConnectedPlayers()) {
            Set<ConnectedClient> players = mpServer.getConnectedPlayers();
            if(players.contains(client)) {
                Server.getLogger().fine("[SERVER] " + clientAddress.getHostAddress() + ":" + clientPort + " disconnected.");
                players.remove(client);
                ((MultiplayerGame) mpServer.getGame()).removePlayer(client);
            }
        }


    }
}
