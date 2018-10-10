import exceptions.FullBox;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Cat extends Thread implements Serializable {
	private static final long serialVersionUID = 1L;
	private final static int CLONE_CHANCE = 1;
	private final static int PURR_CHANCE = 2;
	private final static int TELEPORT_CHANCE = 2;
	private long name;
	private boolean alive;
	private int lives;
	private ReentrantLock lives_lock;
	private transient Box box;
	private Random rand;
	private int battery;


	/**
	 * Creates a new cat with 9 lives and 100 battery
	 */
	Cat() {
		this(9, 100);
	}


	/**
	 * Creates a new cat with the specified number of lives and battery.
	 */
	private Cat(int lives, int battery) {
		this.name = System.nanoTime() % 1000;
		this.lives = lives;
		this.alive = true;
		this.battery = battery;
		rand = new Random();
		lives_lock = new ReentrantLock();
		if(BoxManager.isDebugModeOn()) System.out.println(this + " started");
	}

	@Override
	public void run() {
		while(alive) {
			int n = rand.nextInt(CLONE_CHANCE + PURR_CHANCE + TELEPORT_CHANCE);
			if(n < CLONE_CHANCE) {
				this.replicate();
			}
			else if(n < (CLONE_CHANCE + TELEPORT_CHANCE)) {
				this.teleport();
			} else {
				this.purr();
			}
		}
	}


	/**
	 * Make the thread of a cat sleep from 2 to 4 seconds.
	 * Also decreases the battery of the cat by 15 units.
	 */
	void purr() {
		int sleepTime = rand.nextInt(2000) + 2000;
		try {
			if(BoxManager.isDebugModeOn()) System.out.println(this + " purrs in " + box + " (" + sleepTime + "s)");
			this.battery = Math.max(0, this.battery-15);
			updateBattery();
			Thread.sleep(sleepTime);	
		} catch(Exception e) {
			System.err.println(this + " died while purring");
			this.kill();
		}
	}


    /**
     * Makes the cat teleport to a random box from the Box Map.
     * Also decreases the battery of the cat by 30.
     */
	void teleport() {
		List<Address> boxes = box.getBoxAddresses();
		if(boxes.size() > 0) {
			Address box2;
			do {
				box2 = boxes.get(rand.nextInt(boxes.size()));
			} while (box.getAddress().equals(box2));
			if(BoxManager.isDebugModeOn())  System.out.println(this + " teleports from " + box + " to " + box2);
			
			this.battery = Math.max(0, this.battery-30);
			updateBattery();
			// Send self
			this.send(box2);
			
			// Delete old self
			this.kill();
			box.removeCat(this);
		} else {
			// No boxes available, purr instead
			purr();
		}
	}

    /**
     * Creates a new cat in the same box, with the same parameters
     */
    void replicate() {
		if(BoxManager.isDebugModeOn()) System.out.println(this + " clones itself");
		try {
			box.addCat(new Cat(this.lives,this.battery));
			this.battery = Math.max(0, this.battery/2);
			updateBattery();
		} catch (FullBox e) {
			if(BoxManager.isDebugModeOn()) System.out.println(this + " failed to clone because the box is full");
		}
	}

    /**
     * Sends a copy of a cat to the box with the specified address
     */
	private void send(Address address) {
		this.decrementLives();
		try {
			String serverAddress = address.getAddress();
			int serverPort = address.getPort();
			Socket clientSocket = new Socket(serverAddress, serverPort);
			ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
			
			oos.writeObject(this);
			oos.flush();
			
			oos.close();
			clientSocket.close();
		} catch (IOException e) {
			System.out.println(this + " died while teleporting to " + address);
		}
	}
	
	void kill() {
		lives_lock.lock();
		try {
			this.alive = false;
		} finally {
			lives_lock.unlock();
		}
	}

	/**
	 * Sets the lives of a cat back to 9
	 */
	void resetLives() {
		lives_lock.lock();
		try {
			this.lives = 9;
		} finally {
			lives_lock.unlock();
		}
	}
	
	private void decrementLives() {
		lives_lock.lock();
		try {
			this.lives--;
		} finally {
			lives_lock.unlock();
		}
		if(lives<=0) this.kill();
	}


	/**
	 * Transforms a life (if any) into 100 battery.
	 */
	private void updateBattery() {
		if (this.battery<=0) {
			this.decrementLives();
			this.battery = 100;
		}
	}

	@Override
	public String toString() {
		return  "Cat" + Long.toString(name);
	}

	void setBox(Box b) {
		this.box = b;
		if(BoxManager.isDebugModeOn()) System.out.println(this + " is now in " + b);
	}
	
	boolean isLiving() {
		return alive;
	}
	
	int getLives() {
		return this.lives;
	}
	
	int getBattery() {
		return this.battery;
	}
}
