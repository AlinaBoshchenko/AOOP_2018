package Client;

import Server.ServerCommand;
import Server.ServerHandler;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

public class IndependentBot extends MigratoryBot {
    Vector<Socket> connectedServersSockets;

    public IndependentBot(ServerHandler serverHandler, ArrayList<ServerHandler> connectedServers) {
        super(serverHandler, connectedServers, 4);
    }



    @Override
    protected void beBot() throws IOException {
        while (true) {
            ArrayList<String> skippedMessages = new ArrayList<>();
            for(Socket socket : connectedServersSockets) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (bufferedReader.ready()) { //read all the skipped messages from all chats
                    skippedMessages.add(bufferedReader.readLine());
                }
            }
            updateMessage(skippedMessages);
            String botMessage = getMessage();
            if (botMessage != null) {
                for(Socket socket : connectedServersSockets) {
                    socket.getOutputStream().write(botMessage.getBytes());
                }

            }
            if (starvation <= 0) { //if bot starvates, disconnect from a random chat
                Socket socket = connectedServersSockets.remove(random(0, connectedServersSockets.size()));
                socket.getOutputStream().write(ServerCommand.LOGOFF_COMMAND.toString().getBytes());
                if (socket.isConnected()) {
                    socket.close();
                }
                starvation = defaultStarvation;
            }
            else if(starvation % 2 == 0) { //if bot is close to starvation, connect to a new chat
                Socket newSocket = new Socket();
                for(ServerHandler serverHandler : possibleServers) {
                    boolean good = true;
                    for(Socket socket : connectedServersSockets) {
                        if(socket.getPort() == serverHandler.getPort()) {
                            good = false;
                        }
                    }
                    if(good) {
                        newSocket.connect(new InetSocketAddress("localhost", serverHandler.getPort()));
                        break;
                    }

                }
                if(newSocket.isConnected()) {
                    connectedServersSockets.add(newSocket);
                    newSocket.getOutputStream().write(("-login " + botName + "\n").getBytes());
                }
                --starvation;

            }
            try {
                Thread.sleep(random(6000, 10000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
        botName = "I_" + randomNickName;
        serverOut.write(("-login " + botName + "\n").getBytes());
        beBot();

    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
            connectedServersSockets = new Vector<>();
            socket = new Socket();
            socket.connect(new InetSocketAddress(address, port));
            connectedServersSockets.add(socket);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if(socket.isConnected()) {
            try {
                startBot(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
