package aoop.asteroids.gui.actionListeners;

import aoop.asteroids.model.client.ClientPlayer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class joinGameActionListener implements ActionListener {

    private JTextField ipField;
    private JTextField portField;

    public joinGameActionListener(JTextField ipField, JTextField portField) {
        this.ipField = ipField;
        this.portField = portField;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            ClientPlayer client = new ClientPlayer(InetAddress.getByName(ipField.getText()), Integer.parseInt(portField.getText()));
            new Thread(client).start();
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }
    }
}
