package Server;

public class ServerHandler {

    private final int port;
    private ServerInteraction serverInteraction;

    public ServerHandler(int port) {
        serverInteraction = new ServerInteraction(port);
        this.port = port;
        serverInteraction.start();
    }

    public int getPort() {
        return port;
    }

    public ServerInteraction getServerInteraction() {
        return serverInteraction;
    }
}
