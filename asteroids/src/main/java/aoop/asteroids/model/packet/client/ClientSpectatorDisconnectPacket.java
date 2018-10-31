package aoop.asteroids.model.packet.client;

import aoop.asteroids.model.server.ConnectedClient;
import aoop.asteroids.model.server.Server;

import java.net.InetAddress;
import java.util.Set;

public class ClientSpectatorDisconnectPacket extends ClientGamePacket {
    /**
     * This method describes the actions the specified server that received this packet should do.
     *
     * @param server        The server that received the packet
     * @param clientAddress The address of the client that sent the packet
     * @param clientPort    The port of the client that sent the packet
     */
    @Override
    public void handleClientPacket(Server server, InetAddress clientAddress, int clientPort) {
        ConnectedClient client = new ConnectedClient(clientAddress, clientPort);
        synchronized (server.getConnectedSpectators()) {
            Set<ConnectedClient> spectators = server.getConnectedSpectators();
            if(spectators.contains(client)) {
                Server.getLogger().fine("[SERVER] " + clientAddress.getHostAddress() + ":" + clientPort + " disconnected.");
                spectators.remove(client);
            }
        }


    }
}
