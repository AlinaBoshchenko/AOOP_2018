package aoop.asteroids.gui;

import aoop.asteroids.gui.actionListeners.hostGameActionListener;
import aoop.asteroids.gui.actionListeners.joinGameActionListener;
import aoop.asteroids.gui.actionListeners.singleGameActionListener;
import aoop.asteroids.gui.actionListeners.spectateGameActionListener;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {
    final String FRAME_NAME = "Asteroids Game";
    private static JTextField nickNameField;
    private static JButton joinButton;
    private static JButton singleGameButton;
    private static JButton hostButton;
    private static JTextField ipField;
    private static JTextField portField;
    private static JCheckBox spectatableCheckBox;
    private static JTextField maxSpectatorsField;
    private static JTextField totalPlayersField;
    private static JButton spectateButton;

    MainMenu() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        this.setTitle(FRAME_NAME);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(d.width/3, d.height/5));
        addComponentsToPane(this.getContentPane(), d);
        addActionListeners();

        this.pack();
        this.setVisible(true);

    }

    private static void addRow(JComponent jComponent, Container container) {
        jComponent.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(jComponent);
    }

    private static void addComponentsToPane(Container pane, Dimension screenDimension) {
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        createNickNamePanel(pane, screenDimension);
        createSingleGamePanel(pane, screenDimension);
        createMaxClientsPanel(pane, screenDimension);
        createOptionsPanel(pane, screenDimension);
        createHostPanel(pane, screenDimension);
    }

    private static void addActionListeners() {
        singleGameButton.addActionListener(new singleGameActionListener(nickNameField, spectatableCheckBox, portField, maxSpectatorsField));
        spectateButton.addActionListener(new spectateGameActionListener(ipField, portField));
        hostButton.addActionListener(new hostGameActionListener(nickNameField, spectatableCheckBox, portField, maxSpectatorsField, totalPlayersField));
        joinButton.addActionListener(new joinGameActionListener(ipField, portField));

    }

    private static void createHostPanel(Container pane, Dimension screenDimension) {
        JPanel hostPanel = new JPanel(new FlowLayout());
        hostPanel.add(new JLabel("ip: "));
        ipField = new JTextField("255.255.255.0");
        ipField.setPreferredSize(new Dimension(screenDimension.width/8, nickNameField.getPreferredSize().height));
        hostPanel.add(ipField);
        hostPanel.add(new JLabel("port: "));
        portField = new JTextField("9000");
        portField.setPreferredSize(new Dimension(screenDimension.width/20, nickNameField.getPreferredSize().height));
        hostPanel.add(portField);
        addRow(hostPanel, pane);
    }

    private static void createOptionsPanel(Container pane, Dimension screenDimension) {
        JPanel optionsButtonsPanel = new JPanel(new FlowLayout());
        spectateButton = new JButton("Spectate");
        optionsButtonsPanel.add(spectateButton);
        joinButton = new JButton("Join");
        optionsButtonsPanel.add(joinButton);
        hostButton = new JButton("Host");
        optionsButtonsPanel.add(hostButton);
        optionsButtonsPanel.setMaximumSize(new Dimension(screenDimension.width/2, screenDimension.height/20));

        addRow(optionsButtonsPanel, pane);
    }

    private static void createSingleGamePanel(Container pane, Dimension screenDimension) {
        JPanel singleGamePanel = new JPanel(new FlowLayout());
        singleGameButton = new JButton("Start single game");
        singleGamePanel.add(singleGameButton);
        spectatableCheckBox = new JCheckBox("Spectatable");
        singleGamePanel.add(spectatableCheckBox);
        singleGamePanel.setMaximumSize(new Dimension(screenDimension.width/2, screenDimension.height/20));
        addRow(singleGamePanel, pane);
    }

    private static void createMaxClientsPanel(Container pane, Dimension screenDimension) {
        JPanel maxClientsPanel = new JPanel(new FlowLayout());
        maxClientsPanel.add(new JLabel("Maximum spectators: "));
        maxSpectatorsField = new JTextField("2");
        maxSpectatorsField.setPreferredSize(new Dimension(screenDimension.width/50, nickNameField.getPreferredSize().height));
        maxClientsPanel.add(maxSpectatorsField);
        maxClientsPanel.add(new JLabel("Total players: "));
        totalPlayersField = new JTextField("3");
        totalPlayersField.setPreferredSize(new Dimension(screenDimension.width/50, nickNameField.getPreferredSize().height));
        maxClientsPanel.add(totalPlayersField);
        maxClientsPanel.setMaximumSize(new Dimension(screenDimension.width/2, screenDimension.height/20));
        addRow(maxClientsPanel, pane);
    }

    private static void createNickNamePanel(Container pane, Dimension screenDimension) {
        JPanel nickNamePanel = new JPanel(new FlowLayout());
        nickNamePanel.add(new JLabel("Nickname: "));
        nickNameField = new JTextField("Player");
        nickNameField.setPreferredSize(new Dimension(screenDimension.width/7, nickNameField.getPreferredSize().height));
        nickNamePanel.add(nickNameField);
        nickNamePanel.setMaximumSize(new Dimension(screenDimension.width/2, screenDimension.height/20));
        addRow(nickNamePanel, pane);
    }


    public static void main (String [] args)
    {
        JFrame mmFrame = new MainMenu();
    }
}
