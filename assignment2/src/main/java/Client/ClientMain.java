package Client;

import java.io.IOException;

public class ClientMain {
    public void clientSetup() throws IOException {
        ClientHandler client = new ClientHandler("localhost", 8189);

        if (client.connect()) {
            System.out.println("Connection successful.");
            client.login("kobe");
        } else {
            System.err.println("Connection to the server failed..");
        }
    }
}
