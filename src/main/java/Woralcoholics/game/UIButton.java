package Woralcoholics.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class UIButton extends GameObject{


    private Game game;

    private final int width, height, action;
    private int col, row;
    private final int stringWidth, stringHeight, fontsize;
    private String name;
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
                    int action, int upgradeNr, ImgaeGetter an, int col, int row, int stringWidth, int stringHeight,
                    int fontsize) {
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
        this.stringWidth = stringWidth;
        this.stringHeight = stringHeight;
        this.fontsize = fontsize;
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
        g.setFont(new Font("Masked Hero Demo", Font.PLAIN, fontsize));
        g.drawString(name, stringWidth, stringHeight);
    }

    public void action() {
        switch(action) {
            case 1 -> {
                Game.setState(this.nextState);    //switch to next state
            }
            case 2 -> {
                upgrades.getUpgrade(upgradeNr);
                Upgrades.drawRandomUpgrades();
                Game.setState(this.nextState);
                System.out.println("Got this Upgrade: " + name);
            }
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }
}
