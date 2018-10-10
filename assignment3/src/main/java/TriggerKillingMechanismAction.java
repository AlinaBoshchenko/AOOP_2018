import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TriggerKillingMechanismAction implements ActionListener {
    private BoxManagerView boxManagerView;

    TriggerKillingMechanismAction(BoxManagerView boxManagerView) {
        this.boxManagerView = boxManagerView;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String input;
        input = JOptionPane.showInputDialog(boxManagerView, "Give the ip-address and port number or number of the box", "Format = address:port");
        if(input == null) return;
        String[] serverAddressPort = input.split(":");
        if(serverAddressPort.length == 2) {
            try {
                boxManagerView.getManager().killAll(new Address(serverAddressPort[0], Integer.parseInt(serverAddressPort[1])));
            } catch (NumberFormatException e) {
                System.err.println("Argument wrong, insert an integer for the port.");
            }
        } else {
            try {
                int n = Integer.parseInt(serverAddressPort[0]);
                if (n < boxManagerView.getManager().getBoxesAddresses().size()) {
                    boxManagerView.getManager().killAll(n);
                }
            } catch (NumberFormatException e) {
                System.err.println("Argument wrong, insert an integer.");
            }
        }
    }
}
