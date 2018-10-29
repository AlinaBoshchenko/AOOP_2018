package aoop.asteroids.model.client;

import aoop.asteroids.controller.Player;
import aoop.asteroids.gui.AsteroidsFrame;
import aoop.asteroids.model.Game;
import aoop.asteroids.model.Spaceship;
import aoop.asteroids.model.packet.client.ClientAskJoinPacket;
import aoop.asteroids.model.packet.client.ClientAskSpectatePacket;
import aoop.asteroids.model.packet.client.ClientPlayingPacket;
import aoop.asteroids.model.packet.client.ClientSpectatingPacket;
import aoop.asteroids.model.packet.server.ServerUpdatedGamePacket;

import java.net.InetAddress;

public class ClientPlayer extends Client{

    private Spaceship abstractShip;

    public ClientPlayer(InetAddress serverAddress, int serverPort) {
        super(serverAddress, serverPort);
    }


    @Override
    boolean sendRequestPacket(InetAddress inetAddress, int port) {
        return new ClientAskJoinPacket().sendPacket(datagramSocket, inetAddress, port);
    }

    @Override
    void createClientView(ServerUpdatedGamePacket gamePacket) {
        abstractShip = new Spaceship();
        Player player = new Player();
        player.addShip(abstractShip);
        new AsteroidsFrame(gamePacket.getNewGame(), this, player);
    }

    @Override
    void startClientResponseThread() {
        new Thread(() -> {
            while(connected.get()) {
                new ClientPlayingPacket(abstractShip).sendPacket(datagramSocket, serverAddress, serverPort);
                try {
                    Thread.sleep(Game.getGameTickTime());
                } catch (InterruptedException e) {
                    logger.severe("[ERROR] Spectating connection maintaining thread interrupted: " + e.getMessage());
                }
            }
        }).start();

    }

}
