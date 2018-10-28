package aoop.asteroids.gui.actionListeners;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class hostGameActionListener implements ActionListener {
    private JTextField nickNameField;
    private JCheckBox spectatableCheckBox;
    private JTextField portField;
    private JTextField maxSpectatorsField;
    private JTextField totalPlayersField;

    public hostGameActionListener(JTextField nickNameField, JCheckBox spectatableCheckBox, JTextField portField, JTextField maxSpectatorsField, JTextField totalPlayersField) {
        this.nickNameField = nickNameField;
        this.spectatableCheckBox = spectatableCheckBox;
        this.portField = portField;
        this.maxSpectatorsField = maxSpectatorsField;
        this.totalPlayersField = totalPlayersField;
    }

    @Override
    public void actionPerformed(ActionEvent e) {


    }
}
