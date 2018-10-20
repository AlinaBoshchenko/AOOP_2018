package aoop.asteroids.gui;

import aoop.asteroids.gui.actionListeners.singleGameActionListener;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {
    final String FRAME_NAME = "Asteroids Game";
    private static JTextField nickNameField;
    private static JButton join;
    private static JButton singleGameButton1;
    private static JButton hostButton;
    private static JTextField ipField;
    private static JTextField portField;
    private static JCheckBox spectatableCheckBox;

    MainMenu() {
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

        createNickNamePanel(pane, screenDimension);
        createSingleGamePanel(pane, screenDimension);
        createOptionsPanel(pane, screenDimension);
        createHostPanel(pane, screenDimension);

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
        JButton spectateButton = new JButton("Spectate");
        optionsButtonsPanel.add(spectateButton);
        join = new JButton("Join");
        optionsButtonsPanel.add(join);
        hostButton = new JButton("Host");
        optionsButtonsPanel.add(hostButton);
        optionsButtonsPanel.setMaximumSize(new Dimension(screenDimension.width/2, screenDimension.height/20));
        addRow(optionsButtonsPanel, pane);
    }

    private static void createSingleGamePanel(Container pane, Dimension screenDimension) {
        JPanel singleGamePanel = new JPanel(new FlowLayout());
        singleGameButton1 = new JButton("Start single game");
        singleGameButton1.addActionListener(new singleGameActionListener(nickNameField.getText()));
        singleGamePanel.add(singleGameButton1);
        spectatableCheckBox = new JCheckBox("Spectatable");
        singleGamePanel.add(spectatableCheckBox);
        singleGamePanel.setMaximumSize(new Dimension(screenDimension.width/2, screenDimension.height/20));
        addRow(singleGamePanel, pane);
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
