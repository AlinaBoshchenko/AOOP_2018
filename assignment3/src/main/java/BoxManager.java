import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Random;

public class BoxManager extends Observable implements Runnable{
	// Debug variable
	public static boolean debug_mode = true;
	private String name;
	private List<Address> boxes;
	private boolean running;
	private Random rand;
	private static int CAPACITY;
	
	// Constructor
	public BoxManager(String name) {
		this.name = name;
		this.boxes = new ArrayList<Address>();
		new BoxManagerView(this);
		rand = new Random();
		if(BoxManager.debug_mode) System.out.println(this + " started");
		this.setChanged();
		this.notifyObservers();
	}
	
	// Thread loop
	@Override
	public void run() {
		running=true;
		try	{
			while(running) {
				if (rand.nextInt(5) < 3) {
					// manager in idle mode
					if (BoxManager.debug_mode) System.out.println(this + " watches cats purring (4000ms)");
					Thread.sleep(4000);
				} else {
					// manager opens a random box and adds new cats
					if (BoxManager.debug_mode) System.out.println(this + " is opening boxes");
					this.openBoxes(this.boxes.get(rand.nextInt(this.boxes.size())));
					int i = rand.nextInt(10)-3;
					while (i > 0) {
						i--;
						sendCat(this.boxes.get(rand.nextInt(this.boxes.size())));
					}
				}
			}
	    } catch (Exception e) {
	         System.err.println("opening boxes failed");
	    }

	}
	
	
	// Methods
	public void addBox(Box b) {
		this.addBox(b.getAddress());
	}
	
	public void addBox(Address a) {
		for(Address address : this.boxes) {
			if (address.equals(a)) return;
		}
		boxes.add(a);
		sendBoxMaps(this.boxes);
		this.setChanged();
		this.notifyObservers();
	}
	
	// Starts a new box using the button on the manager view
	public void startBox(Address a) {
		Box b = new Box(CAPACITY, new BoxMap(), a);
		Thread t = new Thread(b);
		t.start();
		this.addBox(b.getAddress());
	}
	
	public void removeBox(Address a) {
		Iterator<Address> iter = this.boxes.iterator();
		while(iter.hasNext()) {
			Address address = iter.next();
			if (address.equals(a)) {
				sendCommand("stop", a);
				iter.remove();
			}
			sendBoxMaps(this.boxes);
			this.setChanged();
			this.notifyObservers();
		}
	}
	
	public void removeBox(int i) {
		this.boxes.remove(i);
	}
	
	// Sets all cats in the box to have 9 lives, removes dead cats
	public void openBoxes(Address a) {
		this.sendCommand("openboxes", a);
	}
	
	public void killAll(Address a) {
		this.sendCommand("killall", a);
	}
	
	public void killAll(int a) {
		this.sendCommand("killall", this.boxes.get(a));
	}
	
	public String toString() {
		return "Manager " + name;
	}
	
	// Aborts all box and cat threads in an orderly manner
	public void stop() {
		for(Address b : boxes) {
			sendCommand("stop", b);
		}
	}
	
	// Sends a command (in string form) to a box
	private void sendCommand(String s, Address a) {
		try {
			String serverAddress = a.getName();
			int serverPort = a.getPort();
			Socket clientSocket = new Socket(serverAddress, serverPort);
			ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());

			oos.writeObject(s);
			oos.flush();
			oos.close();
			clientSocket.close();
		} catch (IOException e) {
			System.err.println("Sending command to " + a + " failed");
			e.printStackTrace();
		}
	}
	
	// Getters and setters
	public List<Address> getBoxes() {
		return this.boxes;
	}
	
	public String getBoxListing() {
		String text = "";
		for(int i = 0; i < this.boxes.size(); i++) {
			Address a = this.boxes.get(i);
			text +=  a + "\n";
		}
		return text;
	}
	
	/**
	 * Usage:
	 * java BoxManager									(defaults to 2 boxes and cats)
	 * java BoxManager <boxes> <cats>					(defaults to capacity = 5)
	 * java BoxManager <boxes> <cats> <box_capacity>
	 */
	
	public static void main (String [] args) {	
		// Input arguments
		int NUM_BOXES = 2;
		int NUM_CATS = 2;
		if (args.length >= 2) {
			try {
	            NUM_BOXES = Integer.parseInt(args[0]);
	            NUM_CATS = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				System.err.println("The argument is not a number");
				System.exit(1);
			}
        }
		CAPACITY = 5;
		if (args.length >= 3) {
			try {
				CAPACITY = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				System.err.println("The argument is not a number");
				System.exit(1);
			}
		}
				
		// Initializing manager
		BoxManager manager = new BoxManager("Schr�dinger");
		Thread mt = new Thread(manager);
		mt.start();
		
		// Initializing boxes
		for(int i = 0; i<NUM_BOXES; i++) {
			Box b;
			try {
				b = new Box(CAPACITY, new BoxMap(), new Address(Inet4Address.getLocalHost().getHostAddress(), 9000+i));
				Thread t = new Thread(b);
				t.start();
				manager.addBox(b.getAddress());
			} catch (UnknownHostException e) {
				System.err.println("Unable to automatically get an ip address");
			}
		}
		
		// Initializing cats
		sendCats(manager.getBoxes(), NUM_CATS);
	}
	
	// Send list of available boxes to all boxes
	private static void sendBoxMaps(List<Address> list) {
		Iterator<Address> iter = list.iterator();
		while(iter.hasNext()) {
			try {
				Address a = iter.next();
				String serverAddress = a.getName();
				int serverPort = a.getPort();
				Socket clientSocket = new Socket(serverAddress, serverPort);
				ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());

				oos.writeObject(new BoxMap(list));
				oos.flush();
				
				oos.close();
				clientSocket.close();
			} catch (IOException e) {
				System.err.println("Sending BoxMap to failed");
				iter.remove();
			}
		}
	}

	private static void sendCats(List<Address> list, int i) {
		Random rand = new Random();
		while(i>0) {
			i--;
			Address address = list.get(rand.nextInt(list.size()));
			sendCat(address);
		}
	}
	
	// add new cat to box with address a
	private static void sendCat(Address a) {
		try {
			String serverAddress = a.getName();
			int serverPort = a.getPort();
			Socket clientSocket = new Socket(serverAddress, serverPort);
			ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
			
			oos.writeObject(new Cat());
			oos.flush();
			
			oos.close();
			clientSocket.close();
		} catch (IOException e) {
			System.out.println("A new cat died while teleporting to " + a);
		}
	}
}
