import java.net.Inet4Address;
import java.net.UnknownHostException;

public class Main {

    /**
     * Usage:
     * java Main    									(defaults to 2 boxes and cats)
     * java Main <boxes> <cats>					        (defaults to capacity = 5)
     * java Main <boxes> <cats> <box_capacity>
     */
    public static void main (String [] args) {
        int nrBoxes = 2;
        int nrCats = 2;
        int capacity = 5;
        if (args.length >= 2) {
            try {
                nrBoxes = Integer.parseInt(args[0]);
                nrCats = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("The arguments are not integers.");
                System.exit(1);
            }
        }
        if (args.length == 3) {
            try {
                capacity = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                System.err.println("The argument is not a number.");
                System.exit(1);
            }
        }

        // Initializing manager
        BoxManager manager = new BoxManager("Schrodinger");
        Thread mt = new Thread(manager);
        mt.start();

        // Initializing boxes
        for(int i = 0; i<nrBoxes; i++) {
            Box b;
            try {
                b = new Box(capacity, new BoxMap(), new Address(Inet4Address.getLocalHost().getHostAddress(), 9000+i));
                new BoxView(b);
                Thread t = new Thread(b);
                t.start();
                manager.addBox(b.getAddress());
            } catch (UnknownHostException e) {
                System.err.println("Unable to automatically get an ip address");
            }
        }

        new BoxManagerView(manager);
        BoxManager.teleportCatToRandomAddresses(manager.getBoxesAddresses(), nrCats);
    }

}
