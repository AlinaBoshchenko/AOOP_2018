package aoop.asteroids.model;

import aoop.asteroids.controller.Player;

import java.awt.*;
import java.io.Serializable;
import java.lang.Runnable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Observable;
import java.util.Random;

/**
 *	The game class is the backbone of all simulations of the asteroid game. It 
 *	contains all game object and keeps track of some other required variables 
 *	in order to specify game rules.
 *	<p>
 *	The game rules are as follows:
 *	<ul>
 *		<li> All game objects are updated according to their own rules every 
 *			game tick. </li>
 *		<li> Every 200th game tick a new asteroid is spawn. An asteroid cannot 
 *			spawn within a 50 pixel radius of the player. </li>
 *		<li> There is a maximum amount of asteroids that are allowed to be 
 *			active simultaneously. Asteroids that spawn from destroying a 
 *			larger asteroid do count towards this maximum, but are allowed to 
 *			spawn if maximum is exceeded. </li>
 *		<li> Destroying an asteroid spawns two smaller asteroids. I.e. large 
 *			asteroids spawn two medium asteroids and medium asteroids spawn two 
 *			small asteroids upon destruction. </li>
 *		<li> The player dies upon colliding with either a buller or an 
 *			asteroid. </li>
 *		<li> Destroying every 5th asteroid increases the asteroid limit by 1, 
 *			increasing the difficulty. </li>
 *	</ul>
 *	<p>
 *	This class implements Runnable, so all simulations will be run in its own 
 *	thread. This class extends Observable in order to notify the view element 
 *	of the program, without keeping a reference to those objects.
 *
 *	@author Yannick Stoffers
 */
public class Game extends Observable implements Runnable, Serializable
{

	/** The spaceship of the player. */
	private Spaceship ship;

	/** List of bullets. */
	protected Collection <Bullet> bullets;

	/** List of asteroids. */
	protected Collection <Asteroid> asteroids;

	/** Random number generator. */
	transient protected static Random rng;

	/** Game tick counter for spawning random asteroids. */
	transient protected int cycleCounter;

	/** Asteroid limit. */
	transient protected int asteroidsLimit;

	/**
	 * The game message to display
	 */
	protected String gameMessage = null;

	/**
	 * The number of dots after the message.
	 */
	int nrDots = 0;
	/**
	 * The time between each game tick.
	 */
	protected static final int gameTickTime = 40;

	/**
	 * The number of game ticks passed since the beginning of the game
	 */
	protected long gameTickCount = 0L;

	/**
	 *	Indicates whether the a new game is about to be started. 
	 *
	 *	@see #run()
	 */
	protected boolean aborted;

	/** Initializes a new game from scratch. */
	public Game (Color spaceShipColor)
	{
		Game.rng = new Random ();
		this.ship = new Spaceship ("Player", spaceShipColor);
		this.initGameData ();
	}

	/** Sets all game data to hold the values of a new game. */
	public void initGameData ()
	{
		this.aborted = false;
		this.cycleCounter = 0;
		this.asteroidsLimit = 7;
		this.bullets = new ArrayList <> ();
		this.asteroids = new ArrayList <> ();
		this.ship.reinit ();
		this.ship.resetScore();
		this.gameMessage = null;
		this.nrDots = 0;
	}

	/** 
	 *	Links the given controller to the spaceship. 
	 *
	 *	@param p the controller that is supposed to control the spaceship.
	 */
	public void linkController (Player p)
	{
		p.addShip (this.ship);
	}

	/** 
	 *	Returns a clone of the spaceship, preserving encapsulation. 
	 *
	 *	@return a clone the spaceship.
	 */
	public Spaceship getPlayer ()
	{
		return this.ship.clone ();
	}

	/** 
	 *	Returns a clone of the asteroid set, preserving encapsulation.
	 *
	 *	@return a clone of the asteroid set.
	 */
	public Collection <Asteroid> getAsteroids ()
	{
		Collection <Asteroid> c = new ArrayList <> ();
		for (Asteroid a : this.asteroids) c.add (a.clone ());
		return c;
	}

	/** 
	 *	Returns a clone of the bullet set, preserving encapsulation.
	 *
	 *	@return a clone of the bullet set.
	 */
	public Collection <Bullet> getBullets ()
	{
		Collection <Bullet> c = new ArrayList <> ();
		for (Bullet b : this.bullets) c.add (b.clone ());
		return c;
	}

	/**
	 *	Method invoked at every game tick. It updates all game objects first. 
	 *	Then it adds a bullet if the player is firing. Afterwards it checks all 
	 *	objects for collisions and removes the destroyed objects. Finally the 
	 *	game tick counter is updated and a new asteroid is spawn upon every 
	 *	200th game tick.
	 */
	protected void update()
	{
		for (Asteroid a : this.asteroids) a.nextStep ();
		for (Bullet b : this.bullets) b.nextStep ();
		this.ship.nextStep ();

		if (this.ship.isFiring ())
		{
			double direction = this.ship.getDirection ();
			this.bullets.add (new Bullet(this.ship.getLocation (), this.ship.getVelocityX () + Math.sin (direction) * 15, this.ship.getVelocityY () - Math.cos (direction) * 15));
			this.ship.setFired ();
		}

		this.checkCollisions ();
		this.removeDestroyedObjects ();

		if (this.cycleCounter == 0 && this.asteroids.size () < this.asteroidsLimit) this.addRandomAsteroid ();
		this.cycleCounter++;
		this.cycleCounter %= 200;

		this.setChanged ();
		this.notifyObservers ();
	}

	/** 
	 *	Adds a randomly sized asteroid at least 50 pixels removed from the 
	 *	player.
	 */
	private void addRandomAsteroid ()
	{
		int prob = Game.rng.nextInt (3000);
		Point loc, shipLoc = this.ship.getLocation ();
		int x, y;
		do
		{
			loc = new Point (Game.rng.nextInt (800), Game.rng.nextInt (800));
			x = loc.x - shipLoc.x;
			y = loc.y - shipLoc.y;
		}
		while (Math.sqrt (x * x + y * y) < 50);

		if (prob < 1000)		this.asteroids.add (new LargeAsteroid  (loc, Game.rng.nextDouble () * 6 - 3, Game.rng.nextDouble () * 6 - 3));
		else if (prob < 2000)	this.asteroids.add (new MediumAsteroid (loc, Game.rng.nextDouble () * 6 - 3, Game.rng.nextDouble () * 6 - 3));
		else					this.asteroids.add (new SmallAsteroid  (loc, Game.rng.nextDouble () * 6 - 3, Game.rng.nextDouble () * 6 - 3));
	}

	/** 
	 *	Checks all objects for collisions and marks them as destroyed upon
	 *	collision. All objects can collide with objects of a different type, 
	 *	but not with objects of the same type. I.e. bullets cannot collide with 
	 *	bullets etc.
	 */
	private void checkCollisions ()
	{ // Destroy all objects that collide.
		for (Bullet b : this.bullets)
		{ // For all bullets.
			for (Asteroid a : this.asteroids)
			{ // Check all bullet/asteroid combinations.
				if (a.collides (b))
				{ // Collision -> destroy both objects.
					b.destroy ();
					a.destroy ();
				}
			}

			if (b.collides (this.ship))
			{ // Collision with playerß -> destroy both objects
				b.destroy ();
				this.ship.destroy ();
				gameMessage = "Game Over";
			}
		}

		for (Asteroid a : this.asteroids)
		{ // For all asteroids, no cross check with bullets required.
			if (a.collides (this.ship))
			{ // Collision with player -> destroy both objects.
				a.destroy ();
				this.ship.destroy ();
				gameMessage = "Game Over";
			}
		}
	}

	/**
	 * 	Increases the score of the player by one and updates asteroid limit 
	 *	when required.
	 */
	private void increaseScore ()
	{
		this.ship.increaseScore ();
		if (this.ship.getScore () % 5 == 0) this.asteroidsLimit++;
	}

	/**
	 *	Removes all destroyed objects. Destroyed asteroids increase the score 
	 *	and spawn two smaller asteroids if it wasn't a small asteroid. New 
	 *	asteroids are faster than their predecessor and travel in opposite 
	 *	direction.
	 */
	private void removeDestroyedObjects ()
	{
		Collection <Asteroid> newAsts = new ArrayList <> ();
		for (Asteroid a : this.asteroids)
		{
			if (a.isDestroyed ())
			{
				this.increaseScore ();
				Collection <Asteroid> successors = a.getSuccessors ();
				newAsts.addAll (successors);
			}
			else newAsts.add (a);
		}
		this.asteroids = newAsts;

		Collection <Bullet> newBuls = new ArrayList <> ();
		for (Bullet b : this.bullets) if (!b.isDestroyed ()) newBuls.add (b);
		this.bullets = newBuls;
	}

	/**
	 *	Returns whether the game is over. The game is over when the spaceship 
	 *	is destroyed.
	 *
	 *	@return true if game is over, false otherwise.
	 */
	protected boolean isGameOver()
	{
		return this.ship.isDestroyed ();
	}

	/** 
	 *	Aborts the game. 
	 *
	 *	@see #run()
	 */
	public void abort ()
	{
		this.aborted = true;
	}

	/**
	 *	This method allows this object to run in its own thread, making sure 
	 *	that the same thread will not perform non essential computations for
	 *	the game. The thread will not stop running until the program is quit. 
	 *	If the game is aborted or the player died, it will wait 100 
	 *	milliseconds before reevaluating and continuing the simulation. 
	 *	<p>
	 *	While the game is not aborted and the player is still alive, it will 
	 *	measure the time it takes the program to perform a game tick and wait 
	 *	40 minus execution time milliseconds to do it all over again. This 
	 *	allows the game to update every 40th millisecond, thus keeping a steady 
	 *	25 frames per second. 
	 *	<p>
	 *	Decrease waiting time to increase fps. Note 
	 *	however, that all game mechanics will be faster as well. I.e. asteroids 
	 *	will travel faster, bullets will travel faster and the spaceship may 
	 *	not be as easy to control.
	 */
	public void run ()
	{ // Update -> sleep -> update -> sleep -> etc...
		long executionTime, sleepTime;
		while (true)
		{
			if (!this.isGameOver() && !this.aborted)
			{
				executionTime = System.currentTimeMillis ();
				this.update ();
				executionTime -= System.currentTimeMillis ();
				sleepTime = Math.max (0, gameTickTime + executionTime);
			}
			else {
				sleepTime = 100;
				updateMessageDots();
			}
			this.gameTickCount++;
			try
			{
				Thread.sleep (sleepTime);
			}
			catch (InterruptedException e)
			{
				System.err.println ("Could not perfrom action: Thread.sleep(...)");
				System.err.println ("The thread that needed to sleep is the game thread, responsible for the game loop (update -> wait -> update -> etc).");
				e.printStackTrace ();
			}
		}
	}


	protected void updateMessageDots() {
		++nrDots;
		nrDots %= 40;
		setChanged();
		notifyObservers();
	}

	/**
	 * Returns the time between each game tick
	 */
	public static int getGameTickTime() {
		return gameTickTime;
	}

	/**
	 * @return The number of game ticks since the start of the game
	 */
	public long getGameTickCount() {
		return gameTickCount;
	}

	/**
	 *  This game returns true if this game is newer (more game ticks elapsed since the start of the game) than the other game.
	 *  Both objects should represent an instance of the same game for this to function correctly.
	 */
	public boolean isNewer(Game other) {
		return this.getGameTickCount() > other.getGameTickCount();
	}

	/**
	 * Returns the game message.
	 * @return the game message as string, null if there is not any.
	 */
	public String getGameMessage() {
		if(gameMessage == null) return null;
		StringBuilder dots = new StringBuilder(4);
		for(int i = 0; i < nrDots/10; ++i) {
			dots.append('.');
		}
		return gameMessage + dots;
	}
}
