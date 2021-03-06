package aoop.asteroids.gui;

import aoop.asteroids.controller.Player;
import aoop.asteroids.model.Game;
import aoop.asteroids.model.client.Client;
import aoop.asteroids.model.client.ClientPlayer;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

/**
 *	AsteroidsFrame is a class that extends JFrame and thus provides a game 
 *	window for the Asteroids game.
 *
 *	@author Yannick Stoffers
 */
public class AsteroidsFrame extends JFrame
{

	/** serialVersionUID */
	public static final long serialVersionUID = 1L;

	/** Quit action. */
	private AbstractAction quitAction;

	/** New game action. */
	private AbstractAction newGameAction;

	/** The game model. */
	private Game game;

	/** The panel in which the game is painted. */
	private AsteroidsPanel ap;

	/** 
	 *	Constructs a new Frame and binds it to a controller, requires a game model.
	 *
	 *	@param game game model.
	 *	@param controller key listener that catches the users actions.
	 */
	public AsteroidsFrame (Game game, Player controller)
	{
		this.game = game;

		this.initActions ();

		this.setTitle ("Asteroids");
		this.setSize (800, 800);
		this.addKeyListener (controller);

		this.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

		JMenuBar mb = new JMenuBar ();
		JMenu m = new JMenu ("Game");
		mb.add (m);
		m.add (this.quitAction);
		m.add (this.newGameAction);
		this.setJMenuBar (mb);

		this.ap = new AsteroidsPanel (this.game);
		this.add (this.ap);
		this.setVisible (true);
	}

	/**
	 *	Constructs a new Frame and binds it to a spectator client.
	 *
	 *	@param client the spectator client that listens to a server for the game packets
	 */
	public AsteroidsFrame(Game game, Client client) {
		this.initActions();

		this.setTitle("Asteroids");
		this.setSize (800, 800);

		this.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

		JMenuBar mb = new JMenuBar ();
		JMenu m = new JMenu ("Game");
		mb.add (m);
		m.add (this.quitAction);
		this.setJMenuBar (mb);

		this.ap = new AsteroidsPanel(game, client);
		this.add(this.ap);
		this.setVisible(true);
	}

	/**
	 *	Constructs a new Frame and binds it to a player client.
	 *
	 *	@param player the player client that listens to a server for the game packets
	 */
	public AsteroidsFrame(Game game, ClientPlayer player, Player controller) {
		this.initActions();

		this.setTitle("Asteroids");
		this.setSize (800, 800);
		this.addKeyListener (controller);

		this.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

		JMenuBar mb = new JMenuBar ();
		JMenu m = new JMenu ("Game");
		mb.add (m);
		m.add (this.quitAction);
		this.setJMenuBar (mb);

		this.ap = new AsteroidsPanel(game, player);
		this.add(this.ap);
		this.setVisible(true);
	}


	/** Quits the old game and starts a new one. */
	private void newGame ()
	{
		this.game.abort ();
		try
		{
			Thread.sleep(50);
		}
		catch (InterruptedException e)
		{
			System.err.println ("Could not sleep before initialing a new game.");
			e.printStackTrace ();
		}
		this.game.initGameData ();
	}

	/** Initializes the quit- and new game action. */
	private void initActions() 
	{
		// Quits the application
		this.quitAction = new AbstractAction ("Quit") 
		{
			public static final long serialVersionUID = 2L;

			@Override
			public void actionPerformed (ActionEvent arg0) 
			{
				System.exit(0);
			}
		};
		
		// Creates a new model
		this.newGameAction = new AbstractAction ("New Game") 
		{
			public static final long serialVersionUID = 3L;

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				AsteroidsFrame.this.newGame ();
			}
		};

	}
	
}
