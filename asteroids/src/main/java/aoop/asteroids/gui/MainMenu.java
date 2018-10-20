package aoop.asteroids.gui;

import aoop.asteroids.gui.actionListeners.singleGameActionListener;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {
    final String FRAME_NAME = "Asteroids Game";

    public MainMenu() {
        //Create and show Main Menu:
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        this.setTitle(FRAME_NAME);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(d.width/3, d.height/5));
        addComponentsToPane(this.getContentPane(), d);


        this.pack();
        this.setVisible(true);

    }

    private static void addRow(JComponent jComponent, Container container) {
        jComponent.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(jComponent);
    }

    private static void addComponentsToPane(Container pane, Dimension screenDimension) {
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        //NickName row:
        JPanel nickNamePanel = new JPanel(new FlowLayout());
        nickNamePanel.add(new JLabel("Nickname: "));
        JTextField nickNameField = new JTextField("Player");
        nickNameField.setPreferredSize(new Dimension(screenDimension.width/7, nickNameField.getPreferredSize().height));
        nickNamePanel.add(nickNameField);
        nickNamePanel.setMaximumSize(new Dimension(screenDimension.width/2, screenDimension.height/20));
        addRow(nickNamePanel, pane);

        //Single Player row:
        JButton singleGameButton = new JButton("Start single game");
        singleGameButton.addActionListener(new singleGameActionListener(nickNameField.getText()));
        addRow(singleGameButton, pane);

        //Join/Spectate row:
        JPanel optionsButtonsPanel = new JPanel(new FlowLayout());
        JButton spectateButton = new JButton("Spectate");
        optionsButtonsPanel.add(spectateButton);
        JButton join = new JButton("Join");
        optionsButtonsPanel.add(join);
        JButton hostButton = new JButton("Host");
        optionsButtonsPanel.add(hostButton);
        optionsButtonsPanel.setMaximumSize(new Dimension(screenDimension.width/2, screenDimension.height/20));
        addRow(optionsButtonsPanel, pane);

        //Host row:
        JPanel hostPanel = new JPanel(new FlowLayout());
        hostPanel.add(new JLabel("ip: "));
        JTextField ipField = new JTextField("255.255.255.0");
        ipField.setPreferredSize(new Dimension(screenDimension.width/8, nickNameField.getPreferredSize().height));
        hostPanel.add(ipField);
        hostPanel.add(new JLabel("port: "));
        JTextField portField = new JTextField("9000");
        portField.setPreferredSize(new Dimension(screenDimension.width/20, nickNameField.getPreferredSize().height));
        hostPanel.add(portField);
        addRow(hostPanel, pane);

    }


    public static void main (String [] args)
    {
        JFrame mmFrame = new MainMenu();
    }
}
