package main.java;

import java.io.*;
import java.net.Socket;

public class ClientHandler {

    private Socket socket;
    private final int port;
    private final String name;
    private OutputStream serverOut;
    private InputStream serverIn;
    private BufferedReader bufferedIn;

    public ClientHandler(String name, int port) {
        this.port = port;
        this.name = name;
    }

    public boolean connect() {
        try {
            this.socket = new Socket(name,port);
            System.out.println("Client port: " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void login(String userID) throws IOException {
        String login = "login " + userID;
        serverOut.write(login.getBytes());

        String response = bufferedIn.readLine();
        System.out.println("Response Line:" + response);
    }
}
