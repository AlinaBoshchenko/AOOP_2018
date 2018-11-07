package aoop.asteroids.gui.actionListeners;


import aoop.asteroids.model.client.Client;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Class that describes the actions application should perform when a player attempts to spectate a game from the main menu of the game.
 */
public class spectateGameActionListener implements ActionListener {

    private JTextField ipField;
    private JTextField portField;

    public spectateGameActionListener(JTextField ipField, JTextField portField) {
        this.ipField = ipField;
        this.portField = portField;
    }

    /**
     * performs action of launching a game spectating
     */

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