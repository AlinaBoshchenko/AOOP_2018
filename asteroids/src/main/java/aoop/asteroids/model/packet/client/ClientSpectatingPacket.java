package aoop.asteroids.model.packet.client;

import aoop.asteroids.model.server.ConnectedClient;
import aoop.asteroids.model.server.Server;
import java.net.InetAddress;
import java.util.Set;

/**
 * This class represents an empty packet a spectator client would send to a server in order to notify it that it continues spectating and didn't time out.
 */
public class ClientSpectatingPacket extends ClientGamePacket {

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
            Set<ConnectedClient> serverConnectedClients = server.getConnectedSpectators();
            for (ConnectedClient iteratedClient : serverConnectedClients) {
                if (iteratedClient.equals(client)) {
                    iteratedClient.refreshTimeoutTicks();
                    return;
                }
            }
        }
    }
}
