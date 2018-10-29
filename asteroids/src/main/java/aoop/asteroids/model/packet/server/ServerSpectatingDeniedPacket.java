package aoop.asteroids.model.packet.server;

import aoop.asteroids.model.client.Client;
import aoop.asteroids.model.packet.GamePacket;

import java.net.InetAddress;

public class ServerSpectatingDeniedPacket extends ServerGamePacket {

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
