package aoop.asteroids.model.server;

import aoop.asteroids.model.Game;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * A class representing a client and all the information related to it a game server requires.
 */
public class ConnectedClient implements Serializable {
    /**
     * THe maximum ticks after which a player is kicked from the server if it doesn't notify the server about it's state.
     */
    private final static int maxTimeoutTicks = 5*1000/Game.getGameTickTime();

    /**
     * The address of the client.
     */
    private final InetAddress inetAddress;
    /**
     * The port from which the client started the connection with the server.
     */
    private final int port;
    /**
     * The ticks after which this connected client will be kicked.
     */
    transient private int timeoutTick;

    /**
     * Creates a new connected client on the specified address and port.
     */
    public ConnectedClient(InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        timeoutTick = maxTimeoutTicks;
        this.port = port;
    }

    /**
     * Gets the address of this client.
     * @return the address.
     */
    InetAddress getInetAddress() {
        return inetAddress;
    }

    /**
     * Refreshes the timeout ticks of this client.
     */
    public void refreshTimeoutTicks() {
        timeoutTick = maxTimeoutTicks;
    }

    /**
     * Decreases the timeout ticks of this client.
     */
    void decreaseTimeoutTicks() {
        --timeoutTick;
    }

    /**
     * Returns the timeout state of this client.
     */
    boolean isTimeouted() {
        return timeoutTick <= 0;
    }

    /**
     * Gets the port from which this client initialized the connection with the server.
     * @return the port
     */
    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ConnectedClient)) {
            return false;
        }
        ConnectedClient other = (ConnectedClient) obj;
        return inetAddress.equals(other.getInetAddress()) && port == other.getPort();
    }

    @Override
    public int hashCode() {
        int result = 42;
        result = 24 * result + inetAddress.hashCode();
        result = 24 * result + port;
        return result;
    }
}
