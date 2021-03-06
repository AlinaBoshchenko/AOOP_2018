package aoop.asteroids;

import aoop.asteroids.gui.AsteroidsFrame;
import aoop.asteroids.controller.Player;
import aoop.asteroids.model.Game;

/**
 *	Main class of the Asteroids program.
 *	<p>
 *	Asteroids is simple game, in which the player is represented by a small 
 *	spaceship. The goal is to destroy as many asteroids as possible and thus 
 *	survive for as long as possible.
 *
 *	@author Yannick Stoffers
 */
public class Asteroids 
{

	/** Constructs a new instance of the program. */
	public Asteroids ()
	{
		Player player = new Player ();
		Game game = new Game ();
		game.linkController (player);
		AsteroidsFrame frame = new AsteroidsFrame (game, player);
		Thread t = new Thread (game);
		t.start ();
	}

}
