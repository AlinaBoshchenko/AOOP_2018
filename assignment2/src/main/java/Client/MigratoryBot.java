package Client;

import Server.ServerCommand;
import Server.ServerHandler;
import Server.ServerInteraction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class MigratoryBot extends LocalBot {
    private ArrayList<ServerHandler> possibleServers;
    private final int defaultStarvation;
    private int starvation;

    public MigratoryBot(ServerHandler serverHandler, ArrayList<ServerHandler> possibleServers) {
        super(serverHandler);
        possibleServers.remove(serverHandler);
        this.possibleServers = new ArrayList<>(possibleServers);
        defaultStarvation = starvation = 2;
    }


    @Override
    protected void starvate() {
        super.starvate();
        --starvation;
    }



    @Override
    protected void beBot(BufferedReader bufferedReader, OutputStream serverOut) throws IOException {
        while (true) {
            ArrayList<String> skippedMessages = new ArrayList<>();
            while (bufferedReader.ready()) { //read all the skipped messages
                skippedMessages.add(bufferedReader.readLine());
            }
            updateMessage(skippedMessages);
            String botMessage = getMessage();
            if(botMessage != null) {
                serverOut.write(botMessage.getBytes());
            }
            if(starvation <= 0) { //if bot starvates, migrate to another random server
                System.out.println("I STARVATED");
                serverOut.write(ServerCommand.LOGOFF_COMMAND.toString().getBytes());
                ServerHandler newServer = possibleServers.remove(random(0, possibleServers.size()));
                possibleServers.add(serverHandler);
                if(socket.isConnected()) {
                    socket.close();
                }
                socket = new Socket("localhost", newServer.getPort());
                address = "localhost";
                port = newServer.getPort();
                serverInteraction = newServer.getServerInteraction();
                serverHandler = newServer;
                serverOut = socket.getOutputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                serverOut.write(("-login " + botName + "\n").getBytes());
                starvation = defaultStarvation;

            }
            try {
                Thread.sleep(random(6000, 10000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
