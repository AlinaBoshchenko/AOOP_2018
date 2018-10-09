import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Cat extends Thread implements Serializable {
	private static final long serialVersionUID = 1L;
	private static int CLONE_CHANCE = 1;
	private static int PURR_CHANCE = 2;
	private static int TELEPORT_CHANCE = 2;
	private long name;
	private boolean alive;
	private int lives;
	private ReentrantLock lives_lock;
	private transient Box box;
	private Random rand;
	private int battery;
	
	// Constructors
	public Cat() {
		this(9, 100);
	}
	
	public Cat(int lives, int battery) {
		this.name = System.nanoTime() % 1000;
		this.lives = lives;
		this.alive = true;
		this.battery = battery;
		rand = new Random();
		lives_lock = new ReentrantLock();
		if(BoxManager.debug_mode) System.out.println(this + " started");
	}
	
	// Thread loop
	@Override
	public void run() {
		while(alive) {
			int  n = rand.nextInt(this.CLONE_CHANCE + this.PURR_CHANCE + this.TELEPORT_CHANCE);
			if( n < this.CLONE_CHANCE) {
				this.cloneCat();
			} else {
				if (n < (this.CLONE_CHANCE + this.TELEPORT_CHANCE)) {
					this.teleport();
				} else {
					this.purr();
				}
			}
		}
	}
	
	// Methods
	// Pauses the thread for a random amount of time
	private void purr() {
		int sleepTime = rand.nextInt(2000)+2000;
		try {
			if(BoxManager.debug_mode) System.out.println(this + " purrs in " + box + " (" + sleepTime + "s)");
			this.battery = Math.max(0, this.battery-15);
			checkBattery();
			Thread.sleep(sleepTime);	
		} catch(Exception e) {
			System.err.println(this + " died while purring");
			this.kill();
		}
	}
	
	// Removes cat from current box, sends cat to random other box
	private void teleport() {
		// Get new box location
		List<Address> boxes = box.getBoxAddresses();
		if(boxes.size() > 0) {
			Address box2;
			do {
				box2 = boxes.get(rand.nextInt(boxes.size()));
			} while (box.getAddress().equals(box2));
			if(BoxManager.debug_mode)  System.out.println(this + " teleports from " + box + " to " + box2);
			
			this.battery = Math.max(0, this.battery-30);
			checkBattery();
			// Send self
			this.send(box2);
			
			// Delete old self
			this.kill();
			box.removeCat(this);
		} else {
			// No boxes available, purr instead
			try {
				int sleepTime = 100;
				if(BoxManager.debug_mode)  System.out.println(this + " purrs in " + box + " (" + sleepTime + "s)");
				this.battery = Math.max(0, this.battery-15);
				checkBattery();	
				Thread.sleep(sleepTime);	
			} catch(Exception e) {
				System.err.println(this + " died while waiting for a teleport");
				this.kill();
			}
		}
	}
	
	// Creates a new cat in the same box, with the same parameters
	private void cloneCat() {
		if(BoxManager.debug_mode) System.out.println(this + " clones itself");
		try {
			box.addCat(new Cat(this.lives,this.battery));
			this.battery = Math.max(0, this.battery/2);
			checkBattery();
		} catch (ArrayIndexOutOfBoundsException e) {
			if(BoxManager.debug_mode) System.out.println(this + " failed to clone because the box is full");
		}
	}
	
	// Sends a copy of a cat to the argument address
	private void send(Address address) {
		this.decrementLives();
		try {
			String serverAddress = address.getName();
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
	
	public void kill() {
		lives_lock.lock();
		try {
			this.alive = false;
		} finally {
			lives_lock.unlock();
		}
	}
	
	public void revive() {
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
	
	// Recharge the battery at the cost of a life
	private void checkBattery() {
		if (this.battery<=0) {
			this.decrementLives();
			this.battery = 100;
		}
	}
	
	public String toString() {
		return  "Cat" + Long.toString(name);
	}
	
	// Getters and setters
	public void setBox(Box b) {
		this.box = b;
		if(BoxManager.debug_mode) System.out.println(this + " is now in " + b);
	}
	
	public boolean getAlive() {
		return alive;
	}
	
	public int getLives() {
		return this.lives;
	}
	
	public int getBattery() {
		return this.battery;
	}
}
