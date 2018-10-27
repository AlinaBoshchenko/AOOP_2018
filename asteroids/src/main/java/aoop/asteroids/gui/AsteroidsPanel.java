package aoop.asteroids.gui;

import aoop.asteroids.model.Asteroid;
import aoop.asteroids.model.Bullet;
import aoop.asteroids.model.Game;
import aoop.asteroids.model.Spaceship;
import aoop.asteroids.model.client.Client;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.lang.Object;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;

/**
 *	AsteroidsPanel extends JPanel and thus provides the actual graphical 
 *	representation of the game model.
 *
 *	@author Yannick Stoffers
 */
public class AsteroidsPanel extends JPanel
{

	/** serialVersionUID */
	public static final long serialVersionUID = 4L;

	/** Game model. */
	private Game game;

	/** 
	 *	Constructs a new game panel, based on the given model.
	 *
	 *	@param game game model.
	 */
	public AsteroidsPanel (Game game)
	{
		this.game = game;
		this.game.addObserver ((o, arg) -> AsteroidsPanel.this.repaint ());
	}


	/**
	 *	Constructs a new game panel, based on the model intercepted by the specified client.
	 *
	 *	@param client game model.
	 */
	public AsteroidsPanel (Game game, Client client)
	{
		this.game = game;
		client.addObserver((o, arg) -> {
			this.game = client.getCurrentGame();
			AsteroidsPanel.this.repaint ();
		});
	}

	/**
	 *	Method for refreshing the GUI.
	 *
	 *	@param g graphics instance to use.
	 */
	@Override
	public void paintComponent (Graphics g)
	{
		super.paintComponent (g);

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		this.setBackground (Color.black);

		this.paintMessage(g2);
		this.paintSpaceship (g2);
		this.paintAsteroids (g2);
		this.paintBullets (g2);

		g2.setFont(getFont().deriveFont(12f));
		g2.setColor (Color.WHITE);
		g2.drawString (String.valueOf (this.game.getPlayer ().getScore ()), 20, 20);
	}

	/**
	 *	Draws the game message if any.
	 *
	 *	@param g2 graphics instance to use.
	 */
	private void paintMessage (Graphics2D g2)
	{
		String gameMessage = game.getGameMessage();
		if(gameMessage != null) {
			g2.setFont(getFont().deriveFont(20f));
			g2.setColor(Color.WHITE);
			FontMetrics fm = g2.getFontMetrics();
			g2.drawString(gameMessage, getWidth() / 2 - fm.stringWidth(gameMessage) / 2, getHeight() / 2 + fm.getAscent() / 2);
		}
	}


	/**
	 *	Draws all bullets in the GUI as a yellow circle.
	 *
	 *	@param g graphics instance to use.
	 */
	private void paintBullets (Graphics2D g)
	{
		g.setColor(Color.yellow);

		for (Bullet b : this.game.getBullets ())
		    g.drawOval (b.getLocation ().x - 2, b.getLocation ().y - 2, 5, 5);
	}

	/**
	 *	Draws all asteroids in the GUI as a filled gray circle.
	 *
	 *	@param g graphics instance to use.
	 */
	private void paintAsteroids (Graphics2D g)
	{
		g.setColor (Color.GRAY);

		for (Asteroid a : this.game.getAsteroids ())
		{
			Ellipse2D.Double e = new Ellipse2D.Double ();
			e.setFrame (a.getLocation ().x - a.getRadius (), a.getLocation ().y - a.getRadius (), 2 * a.getRadius (), 2 * a.getRadius ());
			g.fill (e);
		}
	}

	/**
	 *	Draws the player in the GUI as a see-through white triangle. If the 
	 *	player is accelerating a yellow triangle is drawn as a simple
	 *	representation of flames from the exhaust.
	 *
	 *	@param g graphics instance to use.
	 */
	private void paintSpaceship (Graphics2D g)
	{
		Spaceship s = this.game.getPlayer ();

		// Draw body of the spaceship
		Polygon p = new Polygon ();
		p.addPoint ((int)(s.getLocation ().x + Math.sin (s.getDirection ()				  ) * 20), (int)(s.getLocation ().y - Math.cos (s.getDirection ()				 ) * 20));
		p.addPoint ((int)(s.getLocation ().x + Math.sin (s.getDirection () + 0.8 * Math.PI) * 20), (int)(s.getLocation ().y - Math.cos (s.getDirection () + 0.8 * Math.PI) * 20));
		p.addPoint ((int)(s.getLocation ().x + Math.sin (s.getDirection () + 1.2 * Math.PI) * 20), (int)(s.getLocation ().y - Math.cos (s.getDirection () + 1.2 * Math.PI) * 20));

		g.setColor (s.getColor());
		g.fill (p);
		g.setColor (Color.WHITE);
		g.draw (p);

		// Spaceship accelerating -> continue, otherwise abort.
		if (!s.isAccelerating ()) return;

		// Draw flame at the exhaust
		p = new Polygon ();
		p.addPoint ((int)(s.getLocation ().x - Math.sin (s.getDirection ()				  ) * 25), (int)(s.getLocation ().y + Math.cos (s.getDirection ()				 ) * 25));
		p.addPoint ((int)(s.getLocation ().x + Math.sin (s.getDirection () + 0.9 * Math.PI) * 15), (int)(s.getLocation ().y - Math.cos (s.getDirection () + 0.9 * Math.PI) * 15));
		p.addPoint ((int)(s.getLocation ().x + Math.sin (s.getDirection () + 1.1 * Math.PI) * 15), (int)(s.getLocation ().y - Math.cos (s.getDirection () + 1.1 * Math.PI) * 15));
		g.setColor(Color.yellow);
		g.fill(p);

	}

}
