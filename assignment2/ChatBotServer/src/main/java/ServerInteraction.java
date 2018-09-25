package main.java;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerInteraction extends Thread {

    private final int serverPort;
    private ArrayList<ServerThread> threadList = new ArrayList<>();

    public ServerInteraction(int port) {
        this.serverPort = port;
    }

    public ArrayList<ServerThread> getThreadList () {
        return threadList;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            while(true) {
                System.out.println("Accepting client connection.");
                Socket clientSocket = serverSocket.accept();
                System.out.println(clientSocket + " has connected.");
                ServerThread thread = new ServerThread(this, clientSocket);
                threadList.add(thread);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeUser(ServerThread thread) {
        threadList.remove(thread);
    }
}