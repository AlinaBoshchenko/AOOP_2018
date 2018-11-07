package aoop.asteroids.model.packet.client;

import aoop.asteroids.model.packet.server.ServerJoiningDeniedPacket;
import aoop.asteroids.model.packet.server.ServerUpdatedGamePacket;
import aoop.asteroids.model.server.ConnectedClient;
import aoop.asteroids.model.server.MultiplayerServer;
import aoop.asteroids.model.server.Server;
import java.awt.*;
import java.net.InetAddress;
import java.util.Set;


/**
 * This class represents a packet a client would send to a server when attempting to join it as a player.
 */
public class ClientAskJoinPacket extends ClientGamePacket {
    /**
     * The nick name client wants to take.
     */
    private String nickName;

    /**
     * The desired color of the ship.
     */
    private Color color;

    /**
     * Creates a new packet containing the preferences of an attempting connection player.
     * @param nickName - the nickName the player want to take
     * @param color - the color player desires.
     */
    public ClientAskJoinPacket(String nickName, Color color) {
        this.nickName = nickName;
        this.color = color;
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
        MultiplayerServer multiplayerServer = (MultiplayerServer) server;
        Set <ConnectedClient> serverPlayers = multiplayerServer.getConnectedPlayers();
        if(serverPlayers.size() == multiplayerServer.getPlayerNumber()) {
            new ServerJoiningDeniedPacket().sendPacket(server.getDatagramSocket(), clientAddress, clientPort);
            Server.getLogger().fine("[SERVER] Rejected joining from " + clientAddress.getHostAddress() + ":" + clientPort + ". Reason: All player slots are occupied.");
            return;
        }
        new ServerUpdatedGamePacket(multiplayerServer.getGame()).sendPacket(server.getDatagramSocket(), clientAddress, clientPort);
        Server.getLogger().fine("[SERVER] Sent handshake to " + clientAddress.getHostAddress() + ":" + clientPort);
        multiplayerServer.addNewPlayer(new ConnectedClient(clientAddress, clientPort), nickName, color);
    }
}
