package Woralcoholics.game;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * inspiration
 * @author Christoph Oprawill
 * @author  Maximilian Nowak
 */

public class Bullet extends GameObject {

    public static float bulletSpeed = 30;// 8;

    private final BufferedImage bullet_img;
    protected boolean inGame = false;

    GameManager handler;

    public Bullet(int x, int y, ID id, GameManager handler, ImageGetter an) {
        super(x, y, id, an);
        this.handler = handler;
        //selecting the sprite for the bullet
        bullet_img = an.getImage(2, 3, 64, 64);
    }


    /**
     * map the mouse input to world coordinates
     * anglediff is the angle +- the 2 other bullets of the shotgun have
     *
     * @param mx
     * @param my
     * @param px
     * @param py
     */
    public void direction(double mx, double my, double px, double py, boolean shotgun, float anglediff) {
        if (!shotgun) {
            direction(mx, my, px, py); //normal bullet as usual
        } else {
            direction(mx, my, px, py, anglediff); //bullets for the shotgun
        }
    }

    /**
     * if bullet colloids with block remove it
     */
    public void collision() {   //Collision Detection (Enemys, Blocks)
        for (int i = 0; i < handler.object.size(); i++) {
            GameObject tmpObject = handler.object.get(i);

            if (tmpObject.getId() == ID.Block) {
                if (this.getBounds().intersects(tmpObject.getBounds())) {
                    this.setId(ID.Bullet);
                    this.inGame = false;
                    this.setPos(0,0);
                    //handler.removeObject(this);
                    //System.out.println("Collision");
                }
            }

           /*if (tmpObject.getId() == ID.DestroyableBoxes) {
                if (this.getBounds().intersects(tmpObject.getBounds())) {
                    System.out.println("we made it");
                    boxDestroyedSound();
                    DestroyableBoxes.destroyBox(handler, tmpObject);
                    handler.removeObject(this);
                    //System.out.println("Collision");
                }
            }*/
        }
    }

    /**
     * if bullet gets out of bonce remove it
     */
    public void ooB() {
        for (int i = 0; i < handler.object.size(); i++) {
            GameObject tmp = handler.object.get(i);

            if (tmp.getId() == ID.Bullet) {
                if (x - 4 > 2500 || y - 4 > 1500 || x < 0 || y < 0) {   //Out of Bounds
                    handler.removeObject(this);
                    //System.out.println("OoB");
                }
            }
        }
    }

    private float checkAngle(float angle) {
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    private void direction(double mx, double my, double px, double py) {
        double dx = mx - px;
        double dy = my - py;
        double alpha = Math.atan2(dy, dx);
        velX = (float) (Math.cos(alpha) * bulletSpeed);
        velY = (float) (Math.sin(alpha) * bulletSpeed);
    }

    private void direction(double mx, double my, double px, double py, float anglediff) {
        double dx = mx - px;
        double dy = my - py;
        double alpha = Math.atan2(dy, dx);
        float angle = (float) Math.toDegrees(alpha);
        angle += anglediff; //change for wider range of the rights bullet

        angle = checkAngle(angle); // get's overwritten if angle is not valid
        alpha = Math.toRadians(angle); // overwrite angle

        velX = (float) (Math.cos(alpha) * bulletSpeed);
        velY = (float) (Math.sin(alpha) * bulletSpeed);
    }


    @Override
    public void update() {
            x += velX;
            y += velY;

            collision();
            ooB();
    }

    @Override
    public void render(Graphics g) {
        // out comment part is the collider, still inside for debugging
        /*g.setColor(Color.WHITE);
        g.fillOval((int) x, (int) y, 8, 8);   */
        g.drawImage(bullet_img, (int) x, (int) y, null);

    }

    public void setPos(double x, double y) {
        this.x = (int)x;
        this.y = (int)y;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, 12, 12);
    }

    public float getBulletSpeed() {
        return bulletSpeed;
    }

    public void setBulletSpeed(float bs) {bulletSpeed = bs;}
}

