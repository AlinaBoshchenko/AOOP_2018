package aoop.asteroids.model.packet.server;

import aoop.asteroids.model.client.Client;
import aoop.asteroids.model.packet.GamePacket;
import java.net.InetAddress;

/**
 * Any ServerGamePacket represents a packet which is sent by a game server and received by a client. It extends from a GamePacket,
 * which means it inherits the SendPacket method, that allows the sending of the packet from any "server" DataGram socket. Any concrete
 * class that extends from ServerGamePacket should implement the handleServerPacket method, which takes care of the actions the client that
 * received the packet should take.
 */
public abstract class ServerGamePacket extends GamePacket {

    /**
     *  This method describes the actions the specified client that received this packet should do.
     * @param client The client that received the packet
     * @param serverAddress The address of the server that sent the packet
     * @param serverPort The port of the server that sent the packet
     */
    public abstract void handleServerPacket(Client client, InetAddress serverAddress, int serverPort);
}
