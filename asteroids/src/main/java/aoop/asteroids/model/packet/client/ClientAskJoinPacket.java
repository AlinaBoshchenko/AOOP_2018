package aoop.asteroids.model.packet.client;

import aoop.asteroids.model.packet.server.ServerJoiningDeniedPacket;
import aoop.asteroids.model.packet.server.ServerUpdatedGamePacket;
import aoop.asteroids.model.server.ConnectedClient;
import aoop.asteroids.model.server.MultiplayerServer;
import aoop.asteroids.model.server.Server;

import java.net.InetAddress;
import java.util.Set;

public class ClientAskJoinPacket extends ClientGamePacket {
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
        MultiplayerServer multiplayerServer = (MultiplayerServer) server;
        Set <ConnectedClient> serverPlayers = multiplayerServer.getConnectedPlayers();
        if(serverPlayers.size() == multiplayerServer.getPlayerNumber()) {
            new ServerJoiningDeniedPacket().sendPacket(server.getDatagramSocket(), clientAddress, clientPort);
            Server.getLogger().fine("[SERVER] Rejected joining from " + clientAddress.getHostAddress() + ":" + clientPort + ". Reason: All player slots are occupied.");
            return;
        }
        new ServerUpdatedGamePacket(multiplayerServer.getGame()).sendPacket(server.getDatagramSocket(), clientAddress, clientPort);
        Server.getLogger().fine("[SERVER] Sent handshake to " + clientAddress.getHostAddress() + ":" + clientPort);
        synchronized(multiplayerServer.getConnectedPlayers()) {
            serverPlayers.add(new ConnectedClient(clientAddress, clientPort));
        }
    }
}
