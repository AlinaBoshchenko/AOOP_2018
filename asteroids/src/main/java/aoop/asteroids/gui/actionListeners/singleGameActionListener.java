package aoop.asteroids.gui.actionListeners;

import aoop.asteroids.Asteroids;
import aoop.asteroids.model.server.Server;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class singleGameActionListener implements ActionListener {
    private JTextField nickNameField;
    private JCheckBox spectatableCheckBox;
    private JTextField portField;
    private JTextField maxClientsField;

    public singleGameActionListener(JTextField nickNameField, JCheckBox spectatableCheckBox, JTextField portField, JTextField maxClientsField) {
        this.nickNameField = nickNameField;
        this.spectatableCheckBox = spectatableCheckBox;
        this.portField = portField;
        this.maxClientsField = maxClientsField;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (System.getProperty ("os.name").contains ("Mac"))
        {
            System.setProperty ("apple.laf.useScreenMenuBar", "true");
        }
        new Asteroids();
        if(spectatableCheckBox.isSelected()) {
            Server server = new Server(Integer.parseInt(portField.getText()), Integer.parseInt(maxClientsField.getText()));
            new Thread(server);
            //v rot ebal
        }
    }
}
