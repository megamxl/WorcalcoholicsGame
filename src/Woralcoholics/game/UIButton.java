package Woralcoholics.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class UIButton extends GameObject{

    private GameManager handler;
    private Game game;

    private int width, height;
    private int stringWidth, stringHeight, fontsize;
    private String name;
    private BufferedImage sprite = null;

    /**
     *  Constructor for UI buttons
     * @param x X value of the button
     * @param y Y value of the button
     * @param width
     * @param height
     * @param name
     * @param id
     * @param game
     * @param an
     */
    public UIButton(int x, int y, int width, int height, String name, GameState nextState, ID id, Game game, Animations an, int stringWidth, int stringHeight, int fontsize) {
        super(x, y, nextState, id, an);
        this.width = width;
        this.height = height;
        this.name = name;
        this.game = game;
        this.stringWidth = stringWidth;
        this.stringHeight = stringHeight;
        this.fontsize = fontsize;
        sprite = an.getImage(1,1, width, height);
    }

    @Override
    public void update() {

    }

    @Override
    public void render(Graphics g) {
        g.drawImage(sprite, (int)x, (int)y,null);
        /*g.setColor(Color.RED);
        g.fillRect((int)x, (int)y, width, height);*/
        g.setColor(Color.WHITE);
        g.setFont(new Font("Masked Hero Demo", Font.PLAIN, fontsize));
        g.drawString(name, stringWidth, stringHeight);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }
}
