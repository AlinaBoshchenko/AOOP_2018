package Server;

import java.io.*;
import java.net.Socket;


public class ServerThread extends Thread {


    private static final String ADMISSIBLE_NAME_REGEX = "^[a-zA-Z0-9._-]{3,}$";
    private String clientLogin = null;
    private OutputStream outputStream;
    private InputStream inputStream;
    private final Socket clientSocket;
    private final ServerInteraction serverInteraction;

    public ServerThread(ServerInteraction serverInteraction, Socket client){
        this.serverInteraction = serverInteraction;
        this.clientSocket = client;
    }

    public String getClientLogin() {
        return clientLogin;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    private boolean isLoggedIn() {
        return clientLogin != null;
    }

    private void clientThread() throws IOException {
        inputStream = clientSocket.getInputStream();
        outputStream = clientSocket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while((line = reader.readLine()) != null) {

            ServerMessage serverMessage = new ServerMessage(line);
            if(!isLoggedIn()) {
                if(!serverMessage.isCommand() || !serverMessage.getCommand().equals(ServerCommand.LOGIN_COMMAND)) {
                    sendClientMessage("[SERVER] You need to log in first! (use \"" + ServerCommand.LOGIN_COMMAND + "\" command)\n");
                    continue;
                }
                loginHandler(serverMessage.getArguments());
                continue;
            }
            if(serverMessage.isCommand()) {
                switch (serverMessage.getCommand()) {
                    case LOGIN_COMMAND:
                        sendClientMessage("[SERVER] You are already logged in! To quit use " + ServerCommand.LOGOFF_COMMAND + " command.\n");
                        break;
                    case LOGOFF_COMMAND:
                        logOffHandler();
                        return;
                }

            } else {
                if(serverMessage.getArguments().contains("[SERVER]")) {
                    sendClientMessage("[SERVER] You are not allowed to use [SERVER] keyword in your messages.\n");
                } else {
                    sendOtherClientsMessage(clientLogin + ": " + serverMessage.getArguments() + "\n");
                }
            }
        }
        clientSocket.close();
    }

    private void loginHandler (String login) throws IOException{
        if(!login.matches(ADMISSIBLE_NAME_REGEX)) {
            sendClientMessage("[SERVER] Invalid login name! (inadmissible format or characters)\n");
            return;
        }
        for(ServerThread client : serverInteraction.getThreadList()) {
            if(login.equals(client.getClientLogin())) {
                sendClientMessage("[SERVER] Invalid login name! " + login + " is already taken!\n");
                return;
            }
        }
        if(clientLogin == null) {
            sendClientMessage("[SERVER] You logged in as " + login + ".\n");
            sendOtherClientsMessage("[SERVER] " + login + " has logged in. Great him!\n");
        } else {
            sendClientMessage("[SERVER] You changed your login to: " + login + ".\n");
            sendOtherClientsMessage("[SERVER] " + clientLogin + " changed his login to " +  login);
        }
        clientLogin = login;

    }

    private void logOffHandler() throws IOException {
        serverInteraction.removeUser(this);
        sendClientMessage("Bye!\n");
        clientSocket.close();
        if(clientLogin != null) {
            sendOtherClientsMessage("[SERVER] " + clientLogin + " left.\n");

        }
    }

    private void sendClientMessage(String msg) throws IOException {
        outputStream.write(msg.getBytes());
    }

    private void sendOtherClientsMessage(String msg) throws IOException {
        for(ServerThread client : serverInteraction.getThreadList()) {
            if(client.getClientLogin() == null || client.equals(this)) {
                continue;
            }
            client.sendClientMessage(msg);

        }
    }

    @Override
    public void run(){
        try {
            clientThread();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
