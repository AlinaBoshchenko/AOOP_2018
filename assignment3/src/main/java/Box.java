import exceptions.FullBox;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
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

	/**
	 * Creates a new box server with the specified capacity on the indicated address.
	 * The server will belong to the passed Box Map, which means the cats that will connect to this server
	 * will be able to teleport to other servers that belong to the same Box Map.
	 */
	Box(int capacity, BoxMap map, Address address) {
		if(capacity < 0) throw new IllegalArgumentException("Capacity has to be positive");
		this.name = System.nanoTime() % 1000;
		this.capacity = capacity;
		this.map = map;
		this.cats = new ArrayList<>();
		this.address = address;
		this.running = false;
		if(BoxManager.isDebugModeOn()) System.out.println(this + " started at " + address);
		this.setChanged();
		this.notifyObservers();
		rand = new Random();
	}

	@Override
	public void run() {
		running = true;
		ServerSocket ss  = null;
		try {
			ss = new ServerSocket(address.getPort());
		} catch (IOException e) {
			System.err.println("Failed to open server connection.");
			running =false;
		}

		while(running) {
			Socket cs = null;
			try {
				cs = ss.accept();
			} catch (IOException e) {
				System.err.println("Failed to establish connection with client.");
			}
			if(cs == null) continue;
			ObjectInputStream ois;
			Object object = null;
			try {
				ois = new ObjectInputStream(cs.getInputStream());
				object = ois.readObject();
			} catch (IOException | ClassNotFoundException e) {
				System.err.println("Failed to read the input stream.");
			}
			if(object instanceof Cat) {
				Cat cat = (Cat) object;
				try{
					this.addCat(cat);
				} catch (FullBox e) {
					if(BoxManager.isDebugModeOn()) System.out.println(cat + " died after teleporting because the box is full");
				}

			}
			else if(object instanceof BoxMap) {
				this.map = (BoxMap)object;
			}
            else if(object instanceof String) {
				performCommand((String)object);

			} else {
				System.err.println("Received class not found.");
			}
			if (rand.nextInt(10) > 8) {
				if (BoxManager.isDebugModeOn()) System.out.println(this + " triggers killswitch");
				killAllCats();
			}

			try {
				cs.close();
			} catch (IOException e) {
				System.err.println("Failed to close connection with client.");
			}
		}

		try {
			if (ss != null) {
				ss.close();
			}
		} catch (IOException e) {
			System.err.println("Failed to close server connection.");
		}
	}


	/**
	 * Performs the specified command.
	 */
	private void performCommand(String command) {
		switch (command) {
			case "killall":
				this.killAllCats();
				break;
			case "stop":
				this.killAllCats();
				this.cleanUp();
				this.stop();
				this.map = null;
				break;
			case "openboxes":
				cleanUp();
				break;
			default:
				System.err.println("Command not recognized");
		}
	}


	/**
	 * Returns the number of cats in the given box
	 */
	int getNrCats() {
		return getCats().size();
	}

	/**
	 * This method kills all the cats within a box.
	 * Note: it does not remove their bodies
	 */
	void killAllCats() {
		for(Cat cat : cats) {
			cat.kill();
		}
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * This methods removes all the bodies of dead cats from a box.
	 */
	void removeDeadCats() {
		Iterator<Cat> iterator = getCats().iterator();
		while (iterator.hasNext()) {
			Cat cat = iterator.next();
			if(!cat.isLiving()) {
				System.out.println("Dead cat " + cat + " removed from box " + this);
				iterator.remove();
			}
		}
		this.setChanged();
		this.notifyObservers();
	}


	/**
	 * This method resets the lives to all the living cats within the box to 9.
	 */
	private void repairLivingCats() {
		for(Cat cat : getCats()) {
			if(cat.isLiving()) {
				cat.resetLives();
			}
		}
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * This methods removes all the bodies of dead cats from a box and resets the lives of living cats to 9.
	 */
	void cleanUp() {
		removeDeadCats();
		repairLivingCats();
		this.setChanged();
		this.notifyObservers();
	}


	/**
	 * Adds a new cat to this box. Throws FullBox exception if the box is full.
	 */
	void addCat(Cat c) throws FullBox {
		if(cats.size()+1 > this.capacity) throw new FullBox("Box is full");
		cats.add(c);
		c.setBox(this);
		c.start();
		this.setChanged();
		this.notifyObservers();
	}


	/**
	 * Removes a cat from this box.
	 */
	void removeCat(Cat c) {
		cats.remove(c);
		this.setChanged();
		this.notifyObservers();
	}

	@Override
	public String toString() {
		return "Box" + Long.toString(name);
	}

	private void stop() {
		this.running = false;
	}
	

	List<Cat> getCats() {
		return cats;
	}


	/**
	 * Returns the formatted list of cats as a String.
	 */
	String getCatListing() {
		StringBuilder text = new StringBuilder();
		for(Cat cat :cats) {
			text.append(cat);
			if(!cat.isLiving()) {
				text.append(" (dead)\n");
			} else {
				text.append(" (").append(cat.getLives()).append(" lives, ");
				text.append(cat.getBattery()).append("% battery)\n");
			}

		}
		return text.toString();
	}
	
	List<Address> getBoxAddresses() {
		return this.map.getBoxAddresses();
	}
	
	Address getAddress() {
		return this.address;
	}

	int getCapacity() {
		return this.capacity;
	}
	
	Boolean isRunning() {
		return running;
	}

}
