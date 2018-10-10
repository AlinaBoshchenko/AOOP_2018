import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Random;

public class BoxManager extends Observable implements Runnable{
	private String name;
	private List<Address> boxesAddresses;
	private Random rand;
	private static final int DEFAULT_CAPACITY = 5;
	private static final boolean DEBUG_MODE = true;

	/**
	 * Creates a new Box Manager with the specified name.
	 */
	public BoxManager(String name) {
		this.name = name;
		this.boxesAddresses = new ArrayList<>();
		rand = new Random();
		if(BoxManager.DEBUG_MODE) System.out.println(this + " started");
		this.setChanged();
		this.notifyObservers();
	}

	@Override
	public void run() {
		try	{
			while(true) {
				if (rand.nextInt(5) < 3) {
					if (BoxManager.DEBUG_MODE) System.out.println(this + " stays idle (4000ms)");
					Thread.sleep(4000);
				} else {
					if (BoxManager.DEBUG_MODE) System.out.println(this + " is opening boxesAddresses.");
					this.openBoxes(this.boxesAddresses.get(rand.nextInt(this.boxesAddresses.size())));
					int i = rand.nextInt(10)-3;
					while (i > 0) {
						i--;
						teleportCat(this.boxesAddresses.get(rand.nextInt(this.boxesAddresses.size())));
					}
				}
			}
	    } catch (Exception e) {
	         System.err.println("Opening boxesAddresses failed.");
	    }

	}


	/**
	 * Adds the box at the specified address to the Box Manager.
	 */
	void addBox(Address a) {
		for(Address address : this.boxesAddresses) {
			if (address.equals(a)) return;
		}
		boxesAddresses.add(a);
		sendBoxMaps(this.boxesAddresses);
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * Removes a box with the specified address from the Box Manager.
	 */
	void removeBox(Address a) {
		Iterator<Address> iter = this.boxesAddresses.iterator();
		while(iter.hasNext()) {
			Address address = iter.next();
			if (address.equals(a)) {
				sendCommand("stop", a);
				iter.remove();
			}
			sendBoxMaps(this.boxesAddresses);
			this.setChanged();
			this.notifyObservers();
		}
	}
	
	void removeBox(int i) {
		this.boxesAddresses.remove(i);
	}

	// Sets all cats in the box to have 9 lives, removes dead cats
	private void openBoxes(Address a) {
		this.sendCommand("openboxes", a);
	}


	/**
	 * Kill all the cats in the box at the indicated address.
	 */
	void killAll(Address a) {
		this.sendCommand("killall", a);
	}

	/**
	 * Kill all the cats in the box that has the indicated index.
	 */
	void killAll(int a) {
		this.sendCommand("killall", this.boxesAddresses.get(a));
	}

	@Override
	public String toString() {
		return "Manager " + name;
	}
	

	/**
	 * Sends a string command to the specified address.
	 */
	private void sendCommand(String s, Address a) {
		try {
			String serverAddress = a.getAddress();
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
	

	List<Address> getBoxesAddresses() {
		return this.boxesAddresses;
	}


	/**
	 * Gets the formatted list of boxes the Box Manager manages.
	 */
	String getBoxListing() {
		StringBuilder text = new StringBuilder();
		for(Address box : boxesAddresses) {
			text.append(box).append("\n");
		}
		return text.toString();
	}


	private static void sendBoxMaps(List<Address> list) {
		Iterator<Address> iterator = list.iterator();
		while(iterator.hasNext()) {
			try {
				Address a = iterator.next();
				String serverAddress = a.getAddress();
				int serverPort = a.getPort();
				Socket clientSocket = new Socket(serverAddress, serverPort);
				ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());

				oos.writeObject(new BoxMap(list));
				oos.flush();
				
				oos.close();
				clientSocket.close();
			} catch (IOException e) {
				System.err.println("Sending BoxMap to failed");
				iterator.remove();
			}
		}
	}


	/**
	 * Teleports a cat to n random addresses
	 */
	static void teleportCatToRandomAddresses(List<Address> list, int nr) {
		Random rand = new Random();
		if(list.isEmpty()) {
			return;
		}
		while(nr>0) {
			nr--;
			Address address = list.get(rand.nextInt(list.size()));
			teleportCat(address);
		}
	}

	static boolean isDebugModeOn() {
		return DEBUG_MODE;
	}

	/**
	 * Teleports a cat to the given address
	 */
	private static void teleportCat(Address a) {
		try {
			String serverAddress = a.getAddress();
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

	static int getDefaultCapacity() {
		return DEFAULT_CAPACITY;
	}
}
