package aoop.asteroids.model.server;

import aoop.asteroids.model.Game;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

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
    public MultiplayerServer(Game currentGame, int port, int maxSpectators, int playerNumber) {
        super(currentGame, port, maxSpectators);
        this.playerNumber = playerNumber;
        connectedPlayers = Collections.synchronizedSet(new LinkedHashSet<>(playerNumber));
    }

    public Set<ConnectedClient> getConnectedPlayers() {
        return connectedPlayers;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }
}
