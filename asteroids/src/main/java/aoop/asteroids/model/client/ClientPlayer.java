package aoop.asteroids.model.client;

import aoop.asteroids.controller.Player;
import aoop.asteroids.gui.AsteroidsFrame;
import aoop.asteroids.model.Game;
import aoop.asteroids.model.Spaceship;
import aoop.asteroids.model.packet.client.*;
import aoop.asteroids.model.packet.server.ServerUpdatedGamePacket;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;

/**
 * Class responsible for the representation of a player.
 */
public class ClientPlayer extends Client{

    private Spaceship abstractShip;

    private String nickName;

    private Color color;

    public ClientPlayer(InetAddress serverAddress, int serverPort, String nickName, Color color) {
        super(serverAddress, serverPort);
        this.nickName = nickName;
        this.color = color;
    }

    /**
     * sends packet of request
     */
    @Override
    boolean sendRequestPacket(InetAddress inetAddress, int port) {
        return new ClientAskJoinPacket(nickName, color).sendPacket(datagramSocket, inetAddress, port);
    }

    /**
     * creates client's view
     * @param gamePacket the game packet containing the game information.
     */
    @Override
    void createClientView(ServerUpdatedGamePacket gamePacket) {
        abstractShip = new Spaceship("");
        Player player = new Player();
        player.addShip(abstractShip);
        AsteroidsFrame frame = new AsteroidsFrame(gamePacket.getNewGame(), this, player);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new ClientPlayerDisconnectPacket().sendPacket(datagramSocket, serverAddress, serverPort);
                System.exit(0);
            }
        });
    }

    /**
     * starts client's response thread
     */
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
