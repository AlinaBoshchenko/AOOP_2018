package aoop.asteroids.model.packet.client;

import aoop.asteroids.model.packet.GamePacket;
import aoop.asteroids.model.server.Server;
import java.net.InetAddress;

/**
 * Any ClientGamePacket represents a packet which is sent by a game client and received by a server. It extends from a GamePacket,
 * which means it inherits the SendPacket method, that allows the sending of the packet from any "client" DataGram socket. Any concrete
 * class that extends from ClientGamePacket should implement the handleClientPacket method, which takes care of the actions the server that
 * received the packet should take.
 */
public abstract class ClientGamePacket extends GamePacket {

    /**
     *  This method describes the actions the specified server that received this packet should do.
     * @param server The server that received the packet
     * @param clientAddress The address of the client that sent the packet
     * @param clientPort The port of the client that sent the packet
     */
    public abstract void handleClientPacket(Server server, InetAddress clientAddress, int clientPort);
}
