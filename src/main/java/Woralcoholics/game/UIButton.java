package Woralcoholics.game;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 *  @author Maxlimilian Nowak
 */

public class UIButton extends GameObject{

    private Game game;

    private final int width, height, action;
    private int col, row;
    private final int fontsize;
    private double stringX, stringY, string2X, string2Y;
    private String name, name1 = null, name2 = null;
    private Font font;
    private Graphics g;
    private BufferedImage sprite = null;

    Upgrades upgrades;
    private final int upgradeNr;

    /**
     *  Constructor for UI buttons
     * @param x centered X value of the button
     * @param y centered Y value of the button
     * @param width
     * @param height
     * @param name
     * @param id
     * @param game
     * @param an
     */
    public UIButton(int x, int y, int width, int height, String name, GameState nextState, ID id, Game game,
                    int action, int upgradeNr, ImageGetter an, int col, int row, Graphics g, int fontNr, int fontsize) {
        super(x, y, nextState, id, an);
        this.width = width;
        this.height = height;
        this.name = name;
        this.game = game;
        this.action = action;
        upgrades = new Upgrades(game);
        this.upgradeNr = upgradeNr;
        this.col = col;
        this.row = row;
        this.g = g;

        this.fontsize = fontsize;
        switch(fontNr) {
            case 1 -> font = new Font("Masked Hero Demo", Font.PLAIN, fontsize);
            case 2 -> font = new Font("DEBUG FREE TRIAL", Font.PLAIN, fontsize);
            case 3 -> font = new Font("Cyberpunk", Font.PLAIN, fontsize);
        }
        //font = new Font("Masked Hero Demo", Font.PLAIN, fontsize);
        if(fontsize < 30 && name.length() > 10 && name.contains(" ")) {
            String[] temp = name.split(" ");
            name1 = temp[0];
            name2 = temp[1];
            this.stringX = x - g.getFontMetrics(font).stringWidth(name1) / 2.0;
            this.stringY = y - g.getFontMetrics(font).getHeight() / 8.0;
            string2X = x - g.getFontMetrics(font).stringWidth(name2) / 2.0;
            string2Y = y + g.getFontMetrics(font).getHeight() * 5 / 8.0;
        }
        else {
            this.stringX = x - g.getFontMetrics(font).stringWidth(name) / 2.0;
            this.stringY = y + g.getFontMetrics(font).getHeight() / 4.0;
        }

        this.x -= width / 2.0;
        this.y -= height / 2.0;
        sprite = an.getImage(col, row, width, height);

    }

    @Override
    public void update() {

    }

    @Override
    public void render(Graphics g) {
        g.drawImage(sprite, (int)x, (int)y,null);
        g.setColor(Color.WHITE);
        g.setFont(font);
        if(name2 != null) {
            g.drawString(name1, (int)stringX, (int)stringY);
            g.drawString(name2, (int)string2X, (int)string2Y);
        }
        else {
            g.drawString(name, (int)stringX, (int)stringY);
        }
    }

    public void action() {
        switch(action) {
            case 1 -> {
                Game.setState(this.nextState);    //switch to next state
            }
            case 2 -> {     //Get an Upgrade
                upgrades.getUpgrade(upgradeNr);
                Upgrades.drawRandomUpgrades();
                Game.setState(this.nextState);
                System.out.println("Got this Upgrade: " + name);
            }
            case 3 -> {     //Change Sound Level
                switch(name) {
                    case "M" -> GameManager.soundv = 0;
                    case "K" -> GameManager.soundv = 1;
                    case "L" -> GameManager.soundv = 2;
                }
            }
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }
}