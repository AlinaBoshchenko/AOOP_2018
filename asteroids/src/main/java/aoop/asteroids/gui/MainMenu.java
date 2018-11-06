package aoop.asteroids.gui;

import aoop.asteroids.gui.actionListeners.hostGameActionListener;
import aoop.asteroids.gui.actionListeners.joinGameActionListener;
import aoop.asteroids.gui.actionListeners.singleGameActionListener;
import aoop.asteroids.gui.actionListeners.spectateGameActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Class responsible for creation of the main menu window
 */
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
    private static JColorChooser colorChooser;

    MainMenu() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        this.setTitle(FRAME_NAME);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(d.width/3, d.height*3/5));
        addComponentsToPane(this.getContentPane(), d);
        addActionListeners();

        this.pack();
        this.setVisible(true);

    }

    /**
     * adds row in menu
     */
    private static void addRow(JComponent jComponent, Container container) {
        jComponent.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(jComponent);
    }

    /**
     * adds components of the menu
     */
    private static void addComponentsToPane(Container pane, Dimension screenDimension) {
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        createNickNamePanel(pane, screenDimension);
        createSingleGamePanel(pane, screenDimension);
        createMaxClientsPanel(pane, screenDimension);
        createOptionsPanel(pane, screenDimension);
        createHostPanel(pane, screenDimension);
        createColorChooser(pane, screenDimension);
    }

    /**
     * adds action listeners to connect with game states
     */
    private static void addActionListeners() {
        singleGameButton.addActionListener(new singleGameActionListener(nickNameField, spectatableCheckBox, portField, maxSpectatorsField, colorChooser));
        spectateButton.addActionListener(new spectateGameActionListener(ipField, portField));
        hostButton.addActionListener(new hostGameActionListener(nickNameField, spectatableCheckBox, portField, maxSpectatorsField, totalPlayersField, colorChooser));
        joinButton.addActionListener(new joinGameActionListener(ipField, portField, nickNameField, colorChooser));

    }

    /**
     * graphical interpretation of the menu
     */
    private static class PreviewPanel extends JPanel{
        private Color color;
        private final static int START_X = 130;
        PreviewPanel(Color color) {
            super();
            setSize(400, 400);
            setMinimumSize(new Dimension(400, 400));
            setPreferredSize(new Dimension(400, 400));
            setMaximumSize(new Dimension(400, 400));

            this.color = color;
        }
        @Override
        public void paintComponent (Graphics g)
        {
            super.paintComponent (g);
            if(color == null) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g;


            Polygon p = new Polygon();
            p.addPoint(START_X, 90);
            p.addPoint(START_X+50, 0);
            p.addPoint(START_X+100, 90);
            g2.setColor(color);
            g2.fill(p);
            g2.setColor(Color.WHITE);
            g2.draw(p);

            p = new Polygon();
            p.addPoint(START_X+40, 90);
            p.addPoint(START_X+60, 90);
            p.addPoint(START_X+50, 100);
            g2.setColor(Color.yellow);
            g2.fill(p);
        }

        public void setColor(Color color) {
            this.color = color;
        }
    }

    /**
     * creates a panel for the color chose
     */
    private static void createColorChooser(Container pane, Dimension screenDimension) {
        colorChooser = new JColorChooser();
        PreviewPanel previewPanel = new PreviewPanel(colorChooser.getColor());
        colorChooser.getSelectionModel().addChangeListener(e -> {
            previewPanel.setColor(colorChooser.getColor());
            previewPanel.repaint();
        });
        colorChooser.setPreviewPanel(previewPanel);
        addRow(colorChooser, pane);


    }

    /**
     * creates a host panel
     */
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

    /**
     * creates optional panel
     */
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

    /**
     * creates single game panel
     */
    private static void createSingleGamePanel(Container pane, Dimension screenDimension) {
        JPanel singleGamePanel = new JPanel(new FlowLayout());
        singleGameButton = new JButton("Start single game");
        singleGamePanel.add(singleGameButton);
        spectatableCheckBox = new JCheckBox("Spectatable");
        singleGamePanel.add(spectatableCheckBox);
        singleGamePanel.setMaximumSize(new Dimension(screenDimension.width/2, screenDimension.height/20));
        addRow(singleGamePanel, pane);
    }

    /**
     * creates max players panel
     */
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

    /**
     * creates nickname input panel
     */
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
