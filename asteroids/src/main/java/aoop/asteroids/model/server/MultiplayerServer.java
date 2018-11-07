package aoop.asteroids.model.server;


import aoop.asteroids.model.MultiplayerGame;
import aoop.asteroids.model.packet.server.ServerUpdatedGamePacket;

import java.awt.*;
import java.util.*;

/**
 * This class represents a multi player server for a game which can be both joined and spectated..
 */
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

    /**
     * Adds a new player to this server.
     * @param player the required client information.
     * @param nickName the nickName the player attempts to take.
     * @param color the color of the spaceship the player wishes to have
     */
    public void addNewPlayer(ConnectedClient player, String nickName, Color color) {
        synchronized (connectedPlayers) {
            connectedPlayers.add(player);
        }
        ((MultiplayerGame) getGame()).addNewSpaceship(player, nickName, color);
        getLogger().fine(player.getInetAddress().getHostAddress() + ":" + player.getPort() + " has joined the game with nickname " + nickName);
    }

    /**
     * Returns the set of all connected players to this server.
     * @return set of all connected players.
     */
    public Set<ConnectedClient> getConnectedPlayers() {
        return connectedPlayers;
    }

    /**
     * Returns the number of players for the game this server is created for.
     * @return
     */
    public int getPlayerNumber() {
        return playerNumber;
    }

    /**
     * Updates the model of all connected players.
     */
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
