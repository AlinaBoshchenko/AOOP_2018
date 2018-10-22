package aoop.asteroids.model.server;

import java.net.InetAddress;

public class ConnectedClient {
    private final static int maxTimeoutTicks = 5;
    private InetAddress inetAddress;
    private int timeoutTick;

    ConnectedClient(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
        timeoutTick = maxTimeoutTicks;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public int getTimeoutTick() {
        return timeoutTick;
    }

    private void updateConnectionStatus(boolean connected) {
        timeoutTick = connected ? 5 : (timeoutTick - 1);
    }
}
