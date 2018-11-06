package aoop.asteroids.gui.actionListeners;

import aoop.asteroids.database.MainDB;
import aoop.asteroids.model.MultiplayerGame;
import aoop.asteroids.model.client.ClientPlayer;
import aoop.asteroids.model.server.MultiplayerServer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * class responsible for hosting a game action listener
 */
public class hostGameActionListener implements ActionListener {
    private JTextField nickNameField;
    private JCheckBox spectatableCheckBox;
    private JTextField portField;
    private JTextField maxSpectatorsField;
    private JTextField totalPlayersField;
    private JColorChooser colorChooser;

    public hostGameActionListener(JTextField nickNameField, JCheckBox spectatableCheckBox, JTextField portField, JTextField maxSpectatorsField, JTextField totalPlayersField, JColorChooser colorChooser) {
        this.nickNameField = nickNameField;
        this.spectatableCheckBox = spectatableCheckBox;
        this.portField = portField;
        this.maxSpectatorsField = maxSpectatorsField;
        this.totalPlayersField = totalPlayersField;
        this.colorChooser = colorChooser;
    }

    /**
     * performs action of launching a host game
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        int playerNumber = Integer.parseInt(totalPlayersField.getText());
        MultiplayerGame game = new MultiplayerGame (playerNumber, MainDB.connnectDataBase());
        new Thread(game).start();
        MultiplayerServer server = new MultiplayerServer(game, Integer.parseInt(portField.getText()), Integer.parseInt(maxSpectatorsField.getText()), playerNumber);
        new Thread(server).start();
        game.addObserver(server);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        ClientPlayer client;
        try {
            client = new ClientPlayer(InetAddress.getLocalHost(), Integer.parseInt(portField.getText()), nickNameField.getText(), colorChooser.getColor());
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
            return;
        }
        new Thread(client).start();
    }
}
