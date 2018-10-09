import java.io.ObjectInputStream;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Random;

public class Box extends Observable implements Runnable {
	private long name;
	private int capacity;
	private BoxMap map;
	private List<Cat> cats;
	private Address address;
	private boolean running;
	private Random rand;
	
	// Constructor
	public Box(int capacity, BoxMap map, Address address) {
		if(capacity < 0) throw new IllegalArgumentException("Capacity has to be positive");
		this.name = System.nanoTime() % 1000;
		this.capacity = capacity;
		this.map = map;
		this.cats = new ArrayList<Cat>();
		this.address = address;
		this.running = false;
		new BoxView(this);
		if(BoxManager.debug_mode) System.out.println(this + " started at " + address);
		this.setChanged();
		this.notifyObservers();
		rand = new Random();
	}
	
	// Threadloop
	@Override
	public void run() {
		running = true;
		try {
			// Keeps listening for incoming cats
			while(running) {
				// Open connection
				ServerSocket ss  = new ServerSocket(address.getPort());
				Socket cs = ss.accept();
				

				
				// Receive command
				ObjectInputStream ois = new ObjectInputStream(cs.getInputStream());
				
				Object object = ois.readObject();
				// Try to receive a cat
				try{
					Cat cat = (Cat)object;
					if (cat!=null) {
						try{
							this.addCat(cat);
						} catch (ArrayIndexOutOfBoundsException e) {
							if(BoxManager.debug_mode) System.out.println(cat + " died after teleporting because the box is full");
						}
					}
				} catch (ClassCastException e1) {
					// Try to receive BoxMap
					try {
						BoxMap bm = (BoxMap)object;
						if (bm!=null) {
							this.map = bm;
						}
					} catch (ClassCastException e2) {
						// Try to receive command (string)
						try {
							String command = (String)object;
							switch (command) {
								case "killall":
									this.killAllCats();
									break;
								case "stop":
									this.killAllCats();
									this.openBoxes();
									this.stop();
									this.map = null;
									break;
								case "openboxes":
									openBoxes();
									break;
								default:
									System.err.println("Command not recognized");
							}
						} catch (ClassCastException e3) {
							System.err.println("Received class not found");
						}
					}
				}
				
				// Close connection
				cs.close();
				ss.close();
				
				// Trigger killswitch
				if (rand.nextInt(10) > 8) {
					if (BoxManager.debug_mode) System.out.println(this + " triggers killswitch");
					killAllCats();
				}
			}
	    } catch (Exception e) {
	         System.err.println("Receiving command failed");
	         e.printStackTrace();
	    }
	}
	
	// Methods
	public void killAllCats() {
		for(Cat c : cats) {
			c.kill();
		}
		this.setChanged();
		this.notifyObservers();
	}
	
	// Remove dead cats, reset living cats to 9 lives
	public void openBoxes() {
		Iterator<Cat> iter = this.getCats().iterator();
		while(iter.hasNext()) {
			Cat c = iter.next();
			if(c.getAlive()) {
				c.revive();
			} else {
				System.out.println("Dead cat " + c + " removed from box " + this);
				iter.remove();											
			} 
		} 
		this.setChanged();
		this.notifyObservers();
	}

	// Adds new cat to the box if possible, throws exception otherwise
	public void addCat(Cat c) {
		if(cats.size()+1 > this.capacity) throw new ArrayIndexOutOfBoundsException("Box is full");
		cats.add(c);
		c.setBox(this);
		c.start();
		this.setChanged();
		this.notifyObservers();
	}
	
	public void removeCat(Cat c) {
		cats.remove(c);
		this.setChanged();
		this.notifyObservers();
	}
	
	public String toString() {
		return "Box" + Long.toString(name);
	}
	
	public void stop() {
		this.running = false;
	}
	
	// Getters and setters
	public List<Cat> getCats() {
		return cats;
	}
	
	public String getCatListing() {
		String text = "";
		for(int i = 0; i < this.cats.size(); i++) {
			Cat cat = this.cats.get(i);
			if (!cat.getAlive()) {
				text += cat + " (dead)\n";
			} else {
				text +=  cat + " (" + cat.getLives() + " lives, " + cat.getBattery() + "% battery)\n";
			}
		}
		return text;
	}
	
	public List<Address> getBoxAddresses() {
		return this.map.getBoxAddresses();
	}
	
	public Address getAddress() {
		return this.address;
	}

	public int getCapacity() {
		return this.capacity;
	}
	
	public Boolean isRunning() {
		return running;
	}
	
	
	/**
	 * Usage:
	 * Box <capacity><port>
	 */
	public static void main (String [] args) {	
		// Input arguments
		int CAPACITY;
		Address ADDRESS;
		
		if (args.length >= 2) {
			try {
				CAPACITY = Integer.parseInt(args[0]);
				ADDRESS = new Address(Inet4Address.getLocalHost().getHostAddress(), Integer.parseInt(args[1]));
				
				// Creating a new box
				Box b = new Box(CAPACITY,  new BoxMap(), ADDRESS);
				Thread t = new Thread(b);
				t.start();
				
			} catch (NumberFormatException e) {
				System.err.println("Usage: java Box <capacity> <address> <port>");
				System.exit(1);
			} catch (UnknownHostException e) {
				System.err.println("Making Inet4Address failed");
				System.exit(1);
			}
		} else {
			System.err.println("Usage: java Box <capacity> <port>");
			System.exit(1);
		}
	}
}
