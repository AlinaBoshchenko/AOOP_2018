package aoop.asteroids.gui;

import aoop.asteroids.model.*;
import aoop.asteroids.model.client.Client;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.lang.Object;
import java.util.Comparator;
import java.util.Observable;
import java.util.Observer;
import java.util.PriorityQueue;
import javax.swing.JPanel;

/**
 *	AsteroidsPanel extends JPanel and thus provides the actual graphical 
 *	representation of the game model.
 *
 *	@author Yannick Stoffers
 */
public class AsteroidsPanel extends JPanel
{

	/**
	 * The x where the scores start to be drawn.
	 */
	private final static int SCORE_X = 20;

	/**
	 * * The y where the scores start to be drawn.
	 */
	private final static int SCORE_Y = 20;

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

		this.paintSpaceship (g2);
		this.paintAsteroids (g2);
		this.paintBullets (g2);
		this.paintMessage(g2);
		this.paintScores(g2);

	}


	/**
	 * Draws the scores of the player/players.
	 *
	 * @param g2 graphics instance to use.
	 */
	private void paintScores(Graphics2D g2) {
		g2.setFont(getFont().deriveFont(12f));
		g2.setColor (Color.WHITE);
		if(game instanceof MultiplayerGame) {
			MultiplayerGame mpGame = (MultiplayerGame) game;
			int rowHeight = g2.getFontMetrics().getHeight();
			int currentRowY = SCORE_Y;
			PriorityQueue<Spaceship> spaceshipsByScore = new PriorityQueue<>((o1, o2) -> o2.getScore() - o1.getScore());
			spaceshipsByScore.addAll(mpGame.getConnectedSpaceships().values());
			for(Spaceship s: spaceshipsByScore) {
				g2.drawString(s.getNickName() + ": " + s.getScore(), SCORE_X, currentRowY);
				currentRowY += rowHeight;
			}

		} else {
			g2.drawString (String.valueOf (this.game.getPlayer ().getScore ()), 20, 20);
		}

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

	private void paintSpaceships(MultiplayerGame game, Graphics2D g) {
		for(Spaceship s : game.getConnectedSpaceships().values()) {
			if(s.isDestroyed()) {
				continue;
			}
			Polygon p = new Polygon();
			p.addPoint((int) (s.getLocation().x + Math.sin(s.getDirection()) * 20), (int) (s.getLocation().y - Math.cos(s.getDirection()) * 20));
			p.addPoint((int) (s.getLocation().x + Math.sin(s.getDirection() + 0.8 * Math.PI) * 20), (int) (s.getLocation().y - Math.cos(s.getDirection() + 0.8 * Math.PI) * 20));
			p.addPoint((int) (s.getLocation().x + Math.sin(s.getDirection() + 1.2 * Math.PI) * 20), (int) (s.getLocation().y - Math.cos(s.getDirection() + 1.2 * Math.PI) * 20));

			g.setColor(s.getColor());
			g.fill(p);
			g.setColor(Color.WHITE);
			g.draw(p);

			// Spaceship accelerating -> continue, otherwise abort.
			if (!s.isAccelerating()) continue;

			// Draw flame at the exhaust
			p = new Polygon();
			p.addPoint((int) (s.getLocation().x - Math.sin(s.getDirection()) * 25), (int) (s.getLocation().y + Math.cos(s.getDirection()) * 25));
			p.addPoint((int) (s.getLocation().x + Math.sin(s.getDirection() + 0.9 * Math.PI) * 15), (int) (s.getLocation().y - Math.cos(s.getDirection() + 0.9 * Math.PI) * 15));
			p.addPoint((int) (s.getLocation().x + Math.sin(s.getDirection() + 1.1 * Math.PI) * 15), (int) (s.getLocation().y - Math.cos(s.getDirection() + 1.1 * Math.PI) * 15));
			g.setColor(Color.yellow);
			g.fill(p);

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
		if(game instanceof MultiplayerGame) {
			paintSpaceships((MultiplayerGame) game, g);
		} else {
			Spaceship s = this.game.getPlayer();

			// Draw body of the spaceship
			Polygon p = new Polygon();
			p.addPoint((int) (s.getLocation().x + Math.sin(s.getDirection()) * 20), (int) (s.getLocation().y - Math.cos(s.getDirection()) * 20));
			p.addPoint((int) (s.getLocation().x + Math.sin(s.getDirection() + 0.8 * Math.PI) * 20), (int) (s.getLocation().y - Math.cos(s.getDirection() + 0.8 * Math.PI) * 20));
			p.addPoint((int) (s.getLocation().x + Math.sin(s.getDirection() + 1.2 * Math.PI) * 20), (int) (s.getLocation().y - Math.cos(s.getDirection() + 1.2 * Math.PI) * 20));

			g.setColor(s.getColor());
			g.fill(p);
			g.setColor(Color.WHITE);
			g.draw(p);

			// Spaceship accelerating -> continue, otherwise abort.
			if (!s.isAccelerating()) return;

			// Draw flame at the exhaust
			p = new Polygon();
			p.addPoint((int) (s.getLocation().x - Math.sin(s.getDirection()) * 25), (int) (s.getLocation().y + Math.cos(s.getDirection()) * 25));
			p.addPoint((int) (s.getLocation().x + Math.sin(s.getDirection() + 0.9 * Math.PI) * 15), (int) (s.getLocation().y - Math.cos(s.getDirection() + 0.9 * Math.PI) * 15));
			p.addPoint((int) (s.getLocation().x + Math.sin(s.getDirection() + 1.1 * Math.PI) * 15), (int) (s.getLocation().y - Math.cos(s.getDirection() + 1.1 * Math.PI) * 15));
			g.setColor(Color.yellow);
			g.fill(p);
		}

	}

}
