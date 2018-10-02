package Client;

import Server.ServerCommand;
import Server.ServerHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class MigratoryBot extends LocalBot {
    protected ArrayList<ServerHandler> possibleServers;
    protected final int defaultStarvation;
    protected int starvation;

    public MigratoryBot(ServerHandler serverHandler, ArrayList<ServerHandler> possibleServers) {
        this(serverHandler, possibleServers, 2);
    }


    public MigratoryBot(ServerHandler serverHandler, ArrayList<ServerHandler> possibleServers, int defaultStarvation) {
        super(serverHandler);
        possibleServers.remove(serverHandler);
        this.possibleServers = new ArrayList<>(possibleServers);
        this.defaultStarvation = starvation = defaultStarvation;
    }

    @Override
    protected void starvate() {
        super.starvate();
        --starvation;
    }

    @Override
    protected void startBot(Socket socket) throws IOException {
        OutputStream serverOut = socket.getOutputStream();
        String randomNickName = "bot";

        boolean randomNickNameUnique = false;
        while(!randomNickNameUnique) {
            randomNickName = RandomWords.getRandomWord();
            randomNickNameUnique = true;
            for (Thread client : serverInteraction.getThreadList()) {
                if (client.getName().equals(randomNickName)) {
                    randomNickNameUnique = false;
                    break;
                }
            }
        }
        botName = "M_" + randomNickName;
        serverOut.write(("-login " + botName + "\n").getBytes());
        beBot();

    }

    @Override
    protected void beBot() throws IOException {
        while (true) {
            OutputStream serverOut = socket.getOutputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
