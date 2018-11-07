package aoop.asteroids;

import aoop.asteroids.gui.MainMenu;

/**
 *	Main class of the Asteroids program.
 *	<p>
 *	Asteroids is simple game, in which the player is represented by a small 
 *	spaceship. The goal is to destroy as many asteroids as possible and thus 
 *	survive for as long as possible.
 *  The game is subsequently expanded to be spectatable and played within a multiplayer
 *	@author Yannick Stoffers, Nicu Ghidirimschi, Alina Boshchenko
 */
public class Asteroids 
{
	/** Constructs a new instance of the program. */
	public static void main (String [] args)
	{
		new MainMenu();
	}

}
