import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddNewBoxAction implements ActionListener {
    private BoxManagerView boxManagerView;

    AddNewBoxAction(BoxManagerView boxManagerView) {
        this.boxManagerView = boxManagerView;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String input;
        do {
            input = JOptionPane.showInputDialog(boxManagerView, "Give the ip-address and port number of the box", "Format = address:port");
        } while (input != null && !input.contains(":"));
        if(input == null) return;
        String[] serverAddressPort = input.split(":");
        try {
                Box b = new Box(BoxManager.getDefaultCapacity(), new BoxMap(), new Address(serverAddressPort[0], Integer.parseInt(serverAddressPort[1])));
                new BoxView(b);
                Thread t = new Thread(b);
                t.start();
                boxManagerView.getManager().addBox(b.getAddress());
        } catch (NumberFormatException error) {
            System.err.println("Argument wrong, insert an integer for the port");
        }
    }
}
