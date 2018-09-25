package main.java;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerThread extends Thread {

    private String userID = null;
    private String chat = null;
    private String msg = null;
    private OutputStream outputStream;
    private final Socket clientSocket;
    private final ServerInteraction serverInteraction;
    private HashMap<String, ArrayList<String>> chatRoom = new HashMap<>();

    public ServerThread(ServerInteraction serverInteraction, Socket client){
        this.serverInteraction = serverInteraction;
        this.clientSocket = client;
    }

    public String getUserID() {
        return userID;
    }

    public String getChat() {
        return chat;
    }

    private void clientThread() throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        while((msg = reader.readLine()) != null) {
            if ("quit".equalsIgnoreCase(msg) || msg.contains("logoff".toLowerCase())){
                logoffHandler();
                break;
            }
            if (msg.contains("login".toLowerCase())){
                loginHandler(msg,outputStream);
            } else if (msg.contains("join".toLowerCase())){
                chatRoomJoin(msg);
            } else if (msg.contains("leave".toLowerCase())) {
                chatRoomLeave(msg);
            }else {
                chatRoomMessages(": " + msg);
            }
        }
        clientSocket.close();
    }

    private void loginHandler (String msg, OutputStream outputStream) throws IOException{
        if (msg.contains("login")){
            String login = removeCommand(msg,"login");

            this.userID = login;

            outputStream.write((login + " has connected.\n").getBytes());

            String update = userID + " is online.";
            for(ServerThread thread : serverInteraction.getThreadList()) {
                if(!userID.equals(thread.getUserID())) {
                    thread.notifyUsers(update);
                }
            }
        }
    }

    private void logoffHandler() throws IOException {
        serverInteraction.removeUser(this);
        String msg = userID + " is offline.";
        for(ServerThread thread : serverInteraction.getThreadList()) {
            if(!userID.equals(thread.getUserID())) {
                thread.notifyUsers(msg);
            }
        }
        clientSocket.close();
    }

    private void chatRoomJoin(String msg) throws IOException {
        if (msg.contains("join")) {
            String join = removeCommand(msg,"join");
            ArrayList<String> list;
            if(chatRoom.containsKey(join)){
                list = chatRoom.get(join);
                list.add(join);
            } else {
                list = new ArrayList<String>();
                list.add(userID);
                chatRoom.put(join, list);
            }
            this.chat = join;
            chatRoomMessages(" has joined the chat room.");
        }
    }

    private void chatRoomLeave(String msg) throws IOException {
        if (msg.contains("leave")) {
            chatRoomMessages(" has left the chat room.");
            String leave = removeCommand(msg,"leave");
            if(chatRoom.containsKey(leave)) {
                chatRoom.values().remove(userID);
            }
            if(chatRoom.values().isEmpty()){
                chatRoom.remove(leave);
            }
        }
    }

    private void chatRoomMessages(String msg) throws IOException {
        for (ServerThread thread : serverInteraction.getThreadList()) {
            if (thread.chatRoom.containsKey(getChat())){
                System.out.println(thread.getUserID());
                thread.messageUsers(getUserID(),msg);
            }
        }
        System.out.println("\n");
    }

    private void messageUsers(String user, String msg) throws IOException {
        if (userID != null) {
            outputStream.write((user + msg + "\n").getBytes());
        }
    }

    private void notifyUsers(String msg) throws IOException {
        if (userID != null) {
            outputStream.write((msg + "\n").getBytes());
        }
    }

    private String removeCommand(String command, String remove){
        String regex = "\\s*\\b" + remove + "\\b\\s*";
        String update = command.replaceAll(regex,"");
        return update;
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
