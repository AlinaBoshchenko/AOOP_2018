import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class BoxManagerView extends javax.swing.JFrame implements Observer {
	private static final long serialVersionUID = 1L;
	private BoxManager manager;
	private JTextPane boxlisting;
	private JLabel boxLabel;

	BoxManagerView(BoxManager manager) {
		super(manager.toString());
		this.manager = manager;
		manager.addObserver(this);
		this.initUI();
	}
	
	private void initUI() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(300, 300));

        addComponentsToPane(this.getContentPane());

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
	}
	
	private void addComponentsToPane(Container pane) {
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

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
		
		// Add box button:
		JButton button = new JButton("Add new box");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(new AddNewBoxAction(this));
        pane.add(button);
        
        // Remove box button:
 		button = new JButton("Remove box");
 		button.setAlignmentX(Component.CENTER_ALIGNMENT);
 		button.addActionListener(new RemoveBoxAction(this));
 		pane.add(button);
 		
 		// Trigger killing mechanism button:
 		button = new JButton("Trigger killing mechanism");
 		button.setAlignmentX(Component.CENTER_ALIGNMENT);
 		button.addActionListener(new TriggerKillingMechanismAction(this));
 		pane.add(button);
	}

	@Override
	public void update(Observable o, Object arg) {
		boxLabel.setText("Known boxes (" + this.manager.getBoxesAddresses().size() + "):");
		boxlisting.setText(manager.getBoxListing());
	}

	BoxManager getManager() {
		return manager;
	}
}
