import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class BoxManagerView extends javax.swing.JFrame implements Observer {
	private static final long serialVersionUID = 1L;
	public BoxManager manager;
	private JTextPane boxlisting;
	private JLabel boxLabel;

	public BoxManagerView(BoxManager manager) {
		super(manager.toString());
		this.manager = manager;
		manager.addObserver(this);
		this.initUI();
	}
	
	private void initUI() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(300, 300));
        
        //Set up the content pane.
        addComponentsToPane(this.getContentPane());
 
        //Display the window.
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
	}
	
	private void addComponentsToPane(Container pane) {
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		
		// Name label
		boxLabel = new javax.swing.JLabel();
		boxLabel.setText("Known boxes:");
		boxLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		pane.add(boxLabel);
				
		// List of boxes
		JScrollPane list = new javax.swing.JScrollPane();
		list.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		boxlisting = new javax.swing.JTextPane();
		boxlisting.setEditable(false);
        list.setViewportView(boxlisting);
		
		pane.add(list);
		
		// Add box to the manager button
		JButton button = new JButton("Add new box");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
            	try {
	            	String input = "";
					do {
						input = JOptionPane.showInputDialog(BoxManagerView.this, "Give the ip-address and port number of the box", "Format = address:port");
					} while (!input.contains(":"));
					String[] serverAddressPort = input.split(":");
					try {
						BoxManagerView.this.manager.startBox(new Address(serverAddressPort[0], Integer.parseInt(serverAddressPort[1])));
					} catch (NumberFormatException e) {
						System.err.println("Argument wrong, insert an integer for the port");
					}
            	} catch (NullPointerException e) {
					// Adding box aborted by user
				}
            }
        });
        pane.add(button);
        
        // Remove box from the manager button
 		button = new JButton("Remove box");
 		button.setAlignmentX(Component.CENTER_ALIGNMENT);
 		button.addActionListener(new ActionListener() {

 			@Override
 			public void actionPerformed(ActionEvent arg0) {
				try {
					String input = "";
					input = JOptionPane.showInputDialog(BoxManagerView.this, "Give the ip-address and port number or number of the box", "Format = address:port");
					String[] serverAddressPort = input.split(":");
					if(serverAddressPort.length ==2) {
						try {
							BoxManagerView.this.manager.removeBox(new Address(serverAddressPort[0], Integer.parseInt(serverAddressPort[1])));
						} catch (NumberFormatException e) {
							System.err.println("Argument wrong, insert an integer for the port");
						}
					} else {
						try {
							int n = Integer.parseInt(serverAddressPort[0]);
							if (n < BoxManagerView.this.manager.getBoxes().size()) {
								BoxManagerView.this.manager.removeBox(n);
							}
						} catch (NumberFormatException e) {
							System.err.println("Argument wrong, insert an integer");
						}
					}
				} catch (NullPointerException e) {
					// Removing box aborted by user
				}
 			}
 		});
 		pane.add(button);
 		
 		// Kill all cats in box button
 		button = new JButton("Exterminate box");
 		button.setAlignmentX(Component.CENTER_ALIGNMENT);
 		button.addActionListener(new ActionListener() {

 			@Override
 			public void actionPerformed(ActionEvent arg0) {
				try {
					String input = "";
					input = JOptionPane.showInputDialog(BoxManagerView.this, "Give the ip-address and port number or number of the box", "Format = address:port");
					String[] serverAddressPort = input.split(":");
					if(serverAddressPort.length == 2) {
						try {
							BoxManagerView.this.manager.killAll(new Address(serverAddressPort[0], Integer.parseInt(serverAddressPort[1])));
						} catch (NumberFormatException e) {
							System.err.println("Argument wrong, insert an integer for the port");
						}
					} else {
						try {
							int n = Integer.parseInt(serverAddressPort[0]);
							if (n < BoxManagerView.this.manager.getBoxes().size()) {
								BoxManagerView.this.manager.killAll(n);
							}
						} catch (NumberFormatException e) {
							System.err.println("Argument wrong, insert an integer");
						}
					}
				} catch (NullPointerException e) {
					// Removing box aborted by user
				}
 			}
 		});
 		pane.add(button);
	}

	@Override
	public void update(Observable o, Object arg) {
		boxLabel.setText("Known boxes (" + this.manager.getBoxes().size() + "):");
		boxlisting.setText(this.manager.getBoxListing());
	}
}
