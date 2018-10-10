import exceptions.FullBox;

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
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class BoxView extends javax.swing.JFrame implements Observer {
	private static final long serialVersionUID = 1L;
	private Box model;
	private JTextPane catslisting;
	private JLabel capacityLabel;
	private JLabel catsLabel;
	
	BoxView(Box box) {
		super(box.toString());
		this.model = box;
		model.addObserver(this);
		this.init();
	}
	
	private void init() {
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
		JLabel addressLabel = new javax.swing.JLabel();
		addressLabel.setText("Box address: " + model.getAddress());
		addressLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		pane.add(addressLabel);
		
		// Capacity label
		capacityLabel = new javax.swing.JLabel();
		capacityLabel.setText("Capacity:");
		capacityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		pane.add(capacityLabel);
		
		// List of cats label
		catsLabel = new javax.swing.JLabel();
		catsLabel.setText("Cats:");
		catsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		pane.add(catsLabel);
		
		// List of cats
		JScrollPane list = new javax.swing.JScrollPane();
		list.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		catslisting = new javax.swing.JTextPane();
		catslisting.setEditable(false);
        list.setViewportView(catslisting);
		
		pane.add(list);
		
		// Add cat to the box button
		JButton button = new JButton("Add new cat");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(arg0 -> {
			 try {
				 if(model.isRunning())
					 model.addCat(new Cat());
				 else
					 System.err.println("Box is not running");
			 } catch (FullBox e) {
				 System.err.println("Box is full, no cat added");
			 }
		});
        pane.add(button);

		button = new JButton("Open boxes");
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.addActionListener(arg0 -> model.cleanUp());
		pane.add(button);
	}

	@Override
	public void update(Observable o, Object arg) {
		capacityLabel.setText("Capacity: " + model.getCapacity());
		catsLabel.setText("Cats (" + model.getCats().size() + "):");
		catslisting.setText(model.getCatListing());
		
	}
}
