package aoop.asteroids.database;

import java.awt.Color;
import java.awt.Graphics2D;

public class DBState  {

    /** The x and y coordinate of the drawing to the screen */
    private int X = 350, Y;

    protected Database dbManager;


    public void render(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.drawString("NAME       SCORE", X, 30);
        dbManager = new Database();
        if (dbManager != null) {
            Y = 45;
            for (String s : dbManager.getNames()) {
                g.drawString(s, X, Y);
                Y += 20;
            }
            Y = 45;
            for (Integer i : dbManager.getScores()) {
                g.drawString("        "+i, X + 30, Y);
                Y += 20;
            }
        }
        g.drawString("* PRESS ESC TO GO BACK", 340, 790);
    }


    /** Set the manager to null */
    public void destroy() {
        dbManager = null;
    }

}