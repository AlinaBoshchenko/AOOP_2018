package aoop.asteroids.model;


import java.awt.*;

/**
 * This bullet is an extension of the usual bullet. It also contains the player that fired it,
 * in order to increase his/her score when the bullet hits the spaceship of another player.
 */
public class MultiplayerBullet extends Bullet {

    private Spaceship shooter;
    /**
     * Constructs a new bullet using the given location and velocity
     * parameters. The amount of steps the bullet gets to live is by default
     * 60.
     *
     * @param location  location of the bullet.
     * @param velocityX velocity of the bullet as projected on the X-axis.
     * @param velocityY velocity of the bullet as projected on the Y-axis.
     */
    public MultiplayerBullet(Spaceship shooter, Point location, double velocityX, double velocityY) {
        super(location, velocityX, velocityY);
        this.shooter = shooter;
    }

    public Spaceship getShooter() {
        return shooter;
    }
}
