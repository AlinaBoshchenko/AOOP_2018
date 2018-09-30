package Client;

import Server.ServerHandler;
import Server.ServerInteraction;
import com.sun.security.ntlm.Server;

import java.io.*;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class LocalBot extends Thread {

    protected String address;
    protected int port;
    protected Socket socket = new Socket();
    private ArrayList<String> message = new ArrayList<>();
    protected ServerInteraction serverInteraction;
    protected ServerHandler serverHandler;
    protected String botName;

    public LocalBot(ServerHandler serverHandler) {
        this.address = "localhost";
        this.port = serverHandler.getPort();
        this.serverInteraction = serverHandler.getServerInteraction();
        this.serverHandler = serverHandler;
    }

    private static ArrayList<String> stringToWords(String string) {
        if(string.startsWith("[SERVER]")) {
            return null;
        }
        if(string.contains(":")) {
            string = string.substring(string.indexOf(":") + 2);
        }
        return new ArrayList<>(Arrays.asList(string.split("\\s+")));
    }

    protected static int random(int a, int b) {
        return ThreadLocalRandom.current().nextInt(a, b);
    }

    private static char randomEndSign() {
        switch (random(0, 4)) {
            case 0:
                return ' ';
            case 1:
                return '?';
            case 2:
                return '.';
            case 3:
                return '!';

        }
        return ' ';
    }

    double symillarProcentage(ArrayList<String> message1, ArrayList<String> message2) {
        if(message1 == null ||message2 == null || message1.size() == 0 || message2.size() == 0) {
            return 0.0;
        }
        int symillarWords = 0;
        for(String word : message1) {
            if(message2.contains(word)) {
                symillarWords++;
            }
        }
        return symillarWords/((message1.size()+message2.size())/2.0);

    }

    protected void starvate() {
        message = null;
    }

    protected void updateMessage(ArrayList<String> newMessages){
        ArrayList <String> oldMessage = message;
        message = new ArrayList<>();
        if(newMessages == null || newMessages.size() == 0) {
            int j = random(1, 4);
            for(int i = 0; i < j-1; ++i) {
                message.add(RandomWords.getRandomWord());
            }
            message.add(RandomWords.getRandomWord() + randomEndSign());
            return;
        }
        if(random(0, 10) < 2) { //20% to copy a previous message fully
            message = stringToWords(newMessages.get(random(0, newMessages.size())));
        }
        else {
            ArrayList<ArrayList<String>> newMessagesWords = new ArrayList<>();
            int average = 0;
            for(String newMessage : newMessages) {
                ArrayList <String> newMessageWords = stringToWords(newMessage);
                if(newMessageWords != null) {
                    average += newMessageWords.size();
                    newMessagesWords.add(newMessageWords);
                }
            }
            if(newMessagesWords.size() != 0) {
                average /= newMessagesWords.size();
                if (random(0, 100) < 40) { //with 20% chance decrease the average number of words
                    average -= random(0, average + 1);
                }
            } else {
                if(random(0, 100) < 25) {
                    average = random(0, 3);
                }

            }
            if(average == 0) {
                starvate();
                return;
            }
            int selected;
            ArrayList<String> selectedProp;
            for(int i = 0; i < average; ++i) {
                if(newMessagesWords.size() == 0 || random(0, 10) < 1) { //10% to add a new random word instead of a word from a previous message
                    this.message.add(RandomWords.getRandomWord());
                } else {
                    selected = random(0, newMessagesWords.size());
                    selectedProp = newMessagesWords.remove(selected);
                    this.message.add(selectedProp.get(random(0, selectedProp.size())));
                }
            }
            if(symillarProcentage(message,  oldMessage) > 0.8) {
                average = random(1, 3);
                message.clear();
                for(int i = 0; i < average; ++i) {
                    this.message.add(RandomWords.getRandomWord());
                }
            }
        }

    }

    protected String getMessage() {
        if(message == null) {
            return null;
        }
        StringBuilder concatendated = new StringBuilder();
        for(String word : message) {
            concatendated.append(word).append(" ");
        }
        concatendated.append("\n");
        return concatendated.toString();
    }

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
            try {
                Thread.sleep(random(6000, 10000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    private void startBot(Socket socket) throws IOException {
        OutputStream serverOut = socket.getOutputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
        botName = randomNickName;
        serverOut.write(("-login " + randomNickName + "\n").getBytes());
        beBot(bufferedReader, serverOut);

    }


    @Override
    public void run() {
        try {
            Thread.sleep(1000);
            socket.connect(new InetSocketAddress(address, port));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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
