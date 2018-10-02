package view;

import Server.ServerThread;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

public class ChatBox extends JFrame {
    private JTextArea chatArea;

    public ChatBox() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height / 3;
        int width = screenSize.width / 3;
        Dimension d = new Dimension(width, height);
        this.setPreferredSize(d);
        getContentPane().setPreferredSize(d);

        setVisible(true);
        setResizable(false);
        chatArea = new JTextArea("Chat:\n");
        chatArea.setEditable(false);
        JScrollPane sp = new JScrollPane(chatArea);
        chatArea.setBounds(0,0, d.width, d.height);
        this.add(sp);
        pack();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void addNewMessage(String message) {
        chatArea.append(message);
    }
}
