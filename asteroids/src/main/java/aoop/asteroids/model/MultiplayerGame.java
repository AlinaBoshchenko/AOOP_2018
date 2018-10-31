package aoop.asteroids.model;

import aoop.asteroids.model.server.ConnectedClient;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class MultiplayerGame extends Game {

    private final int MAX_COUNTDOWN_SECONDS = 4;

    private int countdownTicks;


    /**
     * The maximum number of total players.
     */
    private int maxPlayerNumber;

    /**
     * The map of connected clients spaceships.
     */
    private ConcurrentHashMap<ConnectedClient, Spaceship> connectedSpaceships;

    /**
     * The current status of the game.
     */
    private boolean isGameStarted;

    /**
     * The maximum tries to build a new asteroid.
     */
    private final static int MAX_RANDOM_ASTEROID_TRIES = 10;

    public MultiplayerGame(int maxPlayerNumber) {
        super(null);
        connectedSpaceships = new ConcurrentHashMap<>(maxPlayerNumber);
        this.maxPlayerNumber = maxPlayerNumber;
        this.isGameStarted = false;
    }

    public void addNewSpaceship(ConnectedClient client, String nickName, Color color) {
        Spaceship newSpaceship = new Spaceship(nickName, color);
        newSpaceship.destroy();
        connectedSpaceships.put(client, newSpaceship);
        updateGameStatus();
    }

    public void updateShipCoordinates(ConnectedClient player, Spaceship abstractShip) {
        if(connectedSpaceships.get(player) == null) {
            return;
        }
        synchronized (connectedSpaceships.get(player)) {
            Spaceship spaceship = connectedSpaceships.get(player);
            spaceship.setUp(abstractShip.isUp());
            spaceship.setLeft(abstractShip.isLeft());
            spaceship.setRight(abstractShip.isRight());
            spaceship.setIsFiring(abstractShip.isFiring());
        }
    }

    public ConcurrentHashMap<ConnectedClient, Spaceship> getConnectedSpaceships() {
        return connectedSpaceships;
    }

    @Override
    protected void update()
    {
        for (Asteroid a : this.asteroids) a.nextStep ();
        for (Bullet b : this.bullets) b.nextStep ();
        for(Spaceship s : connectedSpaceships.values()) {
            if(s.isDestroyed()) {
                continue;
            }
            s.nextStep();
            if (s.isFiring ())
            {
                double direction = s.getDirection ();
                this.bullets.add (new MultiplayerBullet(s, s.getLocation (), s.getVelocityX () + Math.sin (direction) * 15, s.getVelocityY () - Math.cos (direction) * 15));
                s.setFired ();
            }
        }

        this.checkCollisions ();
        this.removeDestroyedObjects ();

        if (this.cycleCounter == 0 && this.asteroids.size () < this.asteroidsLimit) this.addRandomAsteroid ();
        this.cycleCounter++;
        this.cycleCounter %= 200;

        this.setChanged ();
        this.notifyObservers ();
    }

    private void updateGameStatus() {
        if(isGameStarted) {
            int cnt = 0;
            for(Spaceship spaceship : connectedSpaceships.values()) {
                if(!spaceship.isDestroyed()) {
                    if(++cnt > 1) {
                        break;
                    }
                }
            }
            if(cnt <= 1) {
                initGameData();
                isGameStarted = false;
            }
        }
        if(!isGameStarted) {
            if(connectedSpaceships.size() == maxPlayerNumber) {
                for(Spaceship s : connectedSpaceships.values()) {
                    s.reinit();
                }
                isGameStarted = true;
                countdownTicks = MAX_COUNTDOWN_SECONDS*(1000/gameTickTime);
            }
        }

    }



    private void checkCollisions ()
    {
        bulletLoop:
        for (Bullet b : this.bullets)
        {
            for (Asteroid a : this.asteroids)
            {
                if (a.collides (b))
                {
                    b.destroy ();
                    a.destroy ();
                    continue bulletLoop;
                }
            }

            for(Spaceship s : connectedSpaceships.values()) {
                if(s.isDestroyed()) {
                    continue;
                }
                if (b.collides (s))
                {
                    if(b instanceof MultiplayerBullet) {
                        ((MultiplayerBullet) b).getShooter().increaseScore();
                        System.out.println("SCORE: " + ((MultiplayerBullet) b).getShooter().getScore());
                    }
                    b.destroy ();
                    s.destroy ();
                    updateGameStatus();
                    continue bulletLoop;
                }
            }
        }

        for (Asteroid a : this.asteroids)
        {
            for(Spaceship s : connectedSpaceships.values()) {
                if(s.isDestroyed()) {
                    continue;
                }
                if (a.collides (s))
                {
                    a.destroy ();
                    s.destroy ();
                    updateGameStatus();
                }
            }

        }
    }

    private void removeDestroyedObjects ()
    {
        Collection<Asteroid> newAsts = new ArrayList<>();
        for (Asteroid a : this.asteroids)
        {
            if (a.isDestroyed ())
            {
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

    public void removePlayer(ConnectedClient player) {
        connectedSpaceships.remove(player);
        updateGameStatus();
    }

    private void addRandomAsteroid ()
    {
        int prob = Game.rng.nextInt (3000);
        int tries = MAX_RANDOM_ASTEROID_TRIES;
        int x, y;
        Point loc = new Point(400, 400), shipLoc;
        boolean validCandidate = false;
        while(tries > 0) {
            loc = new Point (Game.rng.nextInt (800), Game.rng.nextInt (800));
            validCandidate = true;
            for (Spaceship s : connectedSpaceships.values()) {
                if(s.isDestroyed()) {
                    continue;
                }
                shipLoc = s.getLocation();
                x = loc.x - shipLoc.x;
                y = loc.y - shipLoc.y;
                if(Math.sqrt (x * x + y * y) < 50) {
                    validCandidate = false;
                    --tries;
                    break;
                }
            }
            if(validCandidate) {
                break;
            }
        }
        if(!validCandidate) return;

        if (prob < 1000)		this.asteroids.add (new LargeAsteroid  (loc, Game.rng.nextDouble () * 6 - 3, Game.rng.nextDouble () * 6 - 3));
        else if (prob < 2000)	this.asteroids.add (new MediumAsteroid (loc, Game.rng.nextDouble () * 6 - 3, Game.rng.nextDouble () * 6 - 3));
        else					this.asteroids.add (new SmallAsteroid  (loc, Game.rng.nextDouble () * 6 - 3, Game.rng.nextDouble () * 6 - 3));
    }


    @Override
    public void run ()
    {
        long executionTime, sleepTime;
        while (true)
        {
            if (this.isGameStarted)
            {
                if(countdownTicks > 0) {
                    int countdownSeconds = (countdownTicks--)/(1000/gameTickTime);
                    nrDots = 0;
                    gameMessage = (countdownSeconds == 0) ? "FIGHT!" : Integer.toString(countdownSeconds);
                    if(countdownTicks == 0) {
                        gameMessage = null;
                    }
                    setChanged();
                    notifyObservers();
                    sleepTime = gameTickTime;
                } else {
                    executionTime = System.currentTimeMillis();
                    this.update();
                    executionTime -= System.currentTimeMillis();
                    sleepTime = Math.max(0, gameTickTime + executionTime);
                }
            }
            else{
                gameMessage = "Waiting for players: " + connectedSpaceships.size() + "/" + maxPlayerNumber;
                updateMessageDots();
                sleepTime = 100;
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
}
