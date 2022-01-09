package Woralcoholics.game;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Christoph Oprawill
 */

public class DestroyableBoxes extends GameObject {

    private final BufferedImage destroyable_boxes;


    /**
     * Creates the Wall tile
     *
     * @param x
     * @param y
     * @param id
     * @param an
     * @param col
     * @param row
     */
    public DestroyableBoxes(float x, float y, ID id, ImageGetter an, Integer col, Integer row) {
        super(x, y, id, an);
        // gets the image from the specified column and row from the spritesheet
        destroyable_boxes = an.getImage(col, row, 64, 64);
        // default
    }

    @Override
    public void update() {
    }

    @Override
    public void render(Graphics g) {
        g.drawImage(destroyable_boxes, (int) x, (int) y, null);
        /*Graphics2D g2d = (Graphics2D) g;
        g.setColor(Color.GREEN);
        g2d.draw(getBounds());*/

    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, 64, 64);
    }

    public static void destroyBox(GameManager handler, GameObject tmp){
        handler.removeObject(tmp);
    }

}
