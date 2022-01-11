package Woralcoholics.game;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Christoph Oprawill
 */

public class DestroyableBoxes extends GameObject {

    private final BufferedImage destroyable_boxes;
    public static int maxHp = 100;
    private int hp = 100;
    private GameManager manager;
    private boolean fullycracked =false;



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
    public DestroyableBoxes(float x, float y, ID id, GameManager manager, ImageGetter an, Integer col, Integer row) {
        super(x, y, id, an);
        this.manager = manager;
        // gets the image from the specified column and row from the spritesheet
        destroyable_boxes = an.getImage(col, row, 64, 64);
        hp = maxHp;
        // default
    }
    public void update() {
        collision();
        isCracked();
    }

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

    public static void destroyBox(GameManager handler, GameObject tmp) {
        handler.removeObject(tmp);
    }




    /**
     * check if enemy bounds with an Rectangle bigger than the actual enemy
     *
     * @return
     */
    public Rectangle getBoundsAround() {
        return new Rectangle((int) x, (int) y, 32, 32);
    }

    /**
     * Collision enemy with a block | Collision enemy with player
     */
    public void collision() {
        for (int i = 0; i < manager.object.size(); i++) {

            GameObject tmpObject = manager.object.get(i);


            //if our bullet is colliding with the enemy hp get's -50
            if (tmpObject.getId() == ID.Bullet) {
                if (getBounds().intersects(tmpObject.getBounds())) {
                    //System.out.println("hit");
                    manager.removeObject(tmpObject);
                    boxDestroyedSound();
                    hp -= 50;
                    System.out.println(hp);
                }
            }
        }
    }


    public void isCracked() {
        if (hp <= 0) {
            remove();
            fullycracked = true;
        }
    }


    public void remove() {
        manager.removeObject(this);
    }

    public void removeWithObject(GameObject tempobject) {
        manager.removeObject(tempobject);
    }

    /***
     * Runs the sound if player moves
     */
    private void boxDestroyedSound() {
        try {
            new Thread(() -> {

                try {
                    manager.playSoundDestroyedBox(fullycracked);
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                } catch (UnsupportedAudioFileException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    // e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
