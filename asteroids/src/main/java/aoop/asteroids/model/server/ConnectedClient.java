package aoop.asteroids.model.server;

import aoop.asteroids.model.Game;

import java.net.InetAddress;

public class ConnectedClient {
    private final static int maxTimeoutTicks = 5*1000/Game.getGameTickTime();
    private final InetAddress inetAddress;
    private final int port;
    private int timeoutTick;

    public ConnectedClient(InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        timeoutTick = maxTimeoutTicks;
        this.port = port;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public int getTimeoutTick() {
        return timeoutTick;
    }


    public void refreshTimeoutTicks() {
        timeoutTick = maxTimeoutTicks;
    }

    public void decreaseTimeoutTicks() {
        --timeoutTick;
    }

    public boolean isTimeouted() {
        return timeoutTick <= 0;
    }

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
}
