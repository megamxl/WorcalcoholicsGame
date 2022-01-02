package Woralcoholics.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Block extends GameObject {

    private final BufferedImage block_img;

    /**
     * Creates the Wall tile
     * @param x
     * @param y
     * @param id
     * @param an
     * @param col
     * @param row
     */
    public Block(float x, float y, ID id, ImgaeGetter an, Integer col, Integer row) {

        super(x, y, id, an);

        // gets the image from the specified column and row from the spritesheet
        block_img = an.getImage(col, row,64,64);

        // default
        // block_img = an.getImage(1,1,64,64);
    }

    @Override
    public void update() {

    }

    @Override
    public void render(Graphics g) {
        g.drawImage(block_img, (int)x, (int)y,null);
        /*Graphics2D g2d = (Graphics2D) g;
        g.setColor(Color.GREEN);
        g2d.draw(getBounds());*/
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int)x,(int)y, 64,64);
    }
}
