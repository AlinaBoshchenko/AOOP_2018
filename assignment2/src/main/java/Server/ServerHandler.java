package Server;

public class ServerHandler {

    private final int port = 8189;

    public void portSetup(){
        ServerInteraction server = new ServerInteraction(port);
        server.start();
    }
}
