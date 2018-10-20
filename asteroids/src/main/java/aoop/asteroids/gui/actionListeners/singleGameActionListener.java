package aoop.asteroids.gui.actionListeners;

import aoop.asteroids.Asteroids;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class singleGameActionListener implements ActionListener {
    private String nickName;
    public singleGameActionListener(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (System.getProperty ("os.name").contains ("Mac"))
        {
            System.setProperty ("apple.laf.useScreenMenuBar", "true");
        }
        new Asteroids();
    }
}
