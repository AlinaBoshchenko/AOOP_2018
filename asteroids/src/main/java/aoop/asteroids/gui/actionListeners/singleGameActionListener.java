package aoop.asteroids.gui.actionListeners;

import aoop.asteroids.Asteroids;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class singleGameActionListener implements ActionListener {
    private JTextField nickNameField;
    private JCheckBox spectatableCheckBox;

    public singleGameActionListener(JTextField nickNameField, JCheckBox spectatableCheckBox) {
        this.nickNameField = nickNameField;
        this.spectatableCheckBox = spectatableCheckBox;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (System.getProperty ("os.name").contains ("Mac"))
        {
            System.setProperty ("apple.laf.useScreenMenuBar", "true");
        }
        if(!spectatableCheckBox.isSelected()) {
            new Asteroids();
        } else {
            System.out.println("Multiplayer started with nickname + " + nickNameField.getText());
        }
    }
}
