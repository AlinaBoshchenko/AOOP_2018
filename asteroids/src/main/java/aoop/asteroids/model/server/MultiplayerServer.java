package aoop.asteroids.model.server;


import aoop.asteroids.model.MultiplayerGame;
import aoop.asteroids.model.packet.server.ServerUpdatedGamePacket;

import java.awt.*;
import java.util.*;

public class MultiplayerServer extends Server {

    /**
     * The number of players that will play the game.
     */
    private int playerNumber;

    /**
     * The set of all connected spectators.
     */
    private final Set<ConnectedClient> connectedPlayers;


    /**
     * Creates a new server bound to the currentGame.
     *
     * @param currentGame   the game to which the server is bound
     * @param port          the port on which the server will be created
     * @param maxSpectators the maximum number of spectators that can watch the game
     * @param playerNumber  the number of players that will play the game
     */
    public MultiplayerServer(MultiplayerGame currentGame, int port, int maxSpectators, int playerNumber) {
        super(currentGame, port, maxSpectators);
        this.playerNumber = playerNumber;
        connectedPlayers = Collections.synchronizedSet(new LinkedHashSet<>(playerNumber));
    }

    public void addNewPlayer(ConnectedClient player, String nickName, Color color) {
        synchronized (connectedPlayers) {
            connectedPlayers.add(player);
        }
        ((MultiplayerGame) getGame()).addNewSpaceship(player, nickName, color);
        getLogger().fine(player.getInetAddress().getHostAddress() + ":" + player.getPort() + " has joined the game with nickname " + nickName);
    }

    public Set<ConnectedClient> getConnectedPlayers() {
        return connectedPlayers;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    private void updatePlayers() {
        synchronized (connectedPlayers) {
            Iterator<ConnectedClient> iterator = connectedPlayers.iterator();
            ConnectedClient client;
            while (iterator.hasNext()) {
                client = iterator.next();
                client.decreaseTimeoutTicks();
                if(client.isTimeouted()) {
                    iterator.remove();
                    getLogger().fine("[SERVER] " + client.getInetAddress().getHostAddress() + ":" + client.getPort() + " disconnected.");
                    MultiplayerGame mpGame = (MultiplayerGame) getGame();
                    mpGame.removePlayer(client);
                    continue;
                }
                new ServerUpdatedGamePacket(getGame()).sendPacket(getDatagramSocket(), client.getInetAddress(), client.getPort());
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        updateSpectators();
        updatePlayers();
    }
}
