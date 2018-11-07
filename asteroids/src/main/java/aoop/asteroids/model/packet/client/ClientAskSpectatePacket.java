package aoop.asteroids.model.packet.client;

import aoop.asteroids.model.packet.server.ServerSpectatingDeniedPacket;
import aoop.asteroids.model.packet.server.ServerUpdatedGamePacket;
import aoop.asteroids.model.server.ConnectedClient;
import aoop.asteroids.model.server.Server;
import java.net.InetAddress;
import java.util.Set;

/**
 * This class represents a packet a client would send to a server when attempting to join it as a spectator.
 */
public class ClientAskSpectatePacket extends ClientGamePacket {

    /**
     * This method describes the actions the specified server that received this packet should do.
     *
     * @param server        The server that received the packet
     * @param clientAddress The address of the client that sent the packet
     * @param clientPort    The port of the client that sent the packet
     */
    @Override
    public void handleClientPacket(Server server, InetAddress clientAddress, int clientPort) {
        Set<ConnectedClient> serverConnectedClients = server.getConnectedSpectators();
        if(serverConnectedClients.size() == server.getMaxSpectators()) {
            String reason;
            if(server.getMaxSpectators() == 0) {
                reason = "This server does not accept spectators.";
            } else {
                reason = "All spectators slots are occupied.";
            }

            new ServerSpectatingDeniedPacket(reason).sendPacket(server.getDatagramSocket(), clientAddress, clientPort);
            Server.getLogger().fine("[SERVER] Rejected spectating from " + clientAddress.getHostAddress() + ":" + clientPort + ". Reason: " + reason);
            return;
        }
        new ServerUpdatedGamePacket(server.getGame()).sendPacket(server.getDatagramSocket(), clientAddress, clientPort);
        Server.getLogger().fine("[SERVER] Sent handshake to " + clientAddress.getHostAddress() + ":" + clientPort);
        synchronized(server.getConnectedSpectators()) {
            serverConnectedClients.add(new ConnectedClient(clientAddress, clientPort));
        }
    }
}
