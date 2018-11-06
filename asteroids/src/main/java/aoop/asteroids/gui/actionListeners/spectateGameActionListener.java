package aoop.asteroids.gui.actionListeners;

import aoop.asteroids.gui.AsteroidsFrame;
import aoop.asteroids.model.client.Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
/**
 * class responsible for spectating a game action listener
 */
public class spectateGameActionListener implements ActionListener {

    private JTextField ipField;
    private JTextField portField;

    public spectateGameActionListener(JTextField ipField, JTextField portField) {
        this.ipField = ipField;
        this.portField = portField;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            Client client = new Client(InetAddress.getByName(ipField.getText()), Integer.parseInt(portField.getText()));
            new Thread(client).start();
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }
    }
}