package Woralcoholics.game;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * inspiration
 * @author Lukas Schelepet
 * @author Christoph Oprawill
 * @author Maximilian Nowak
 */

public class Bullet extends GameObject {

    public static float bulletSpeed = 30;   //Speed of the Bullet

    private final BufferedImage bullet_img; //Sprite of the Bullet

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
    public void direction(double mx, double my, double px, double py, boolean shotgun, float anglediff, boolean gunnerenemy) {
        if (!shotgun) {
            if (gunnerenemy) {
                direction(mx, my, px, py, true); //normal bullet as usual for the gunnerenemy
            } else {
                direction(mx, my, px, py, false); //normal bullet as usual for the player
            }
        } else {
            direction(mx, my, px, py, anglediff); //bullets for the shotgun
        }

    }

    /**
     * if bullet collides with block remove it
     */
    public void collision() {   //Collision Detection (Enemys, Blocks)
        for (int i = 0; i < handler.object.size(); i++) {
            GameObject tmpObject = handler.object.get(i);

            if (tmpObject.getId() == ID.Block) {
                if (this.getBounds().intersects(tmpObject.getBounds())) {
                    this.setId(ID.Bullet);
                    this.inGame = false;
                    this.setPos(0, 0);
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
     * if bullet gets out of bounds remove it
     */
    public void ooB() {
        for (int i = 0; i < handler.bullets.size(); i++) {
            Bullet tmp = handler.bullets.get(i);
            if (tmp.x - 4 > 2500 || tmp.y - 4 > 1500 || tmp.x < 0 || tmp.y < 0) {   //Out of Bounds
                tmp.setId(ID.Bullet);
                tmp.inGame = false;
                tmp.setPos(0, 0);
            }
        }
    }

    /***
     * preventing for minus angles
     * @param angle
     * @return
     */
    private float checkAngle(float angle) {
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    private void direction(double mx, double my, double px, double py, boolean gunnerenemy) {
        //calculate angle and x-y-speeds of the bullet
        double dx = mx - px;
        double dy = my - py;
        double alpha = Math.atan2(dy, dx);
        velX = (float) (Math.cos(alpha) * bulletSpeed);
        velY = (float) (Math.sin(alpha) * bulletSpeed);

        switch(handler.selectedWeapon.getType()) {
            case Pistol -> setPos(px+Math.cos(alpha)*30-16, py+Math.sin(alpha)*30-16);
            case MachineGun -> setPos(px+Math.cos(alpha)*50-16, py+Math.sin(alpha)*50-16);
        }
    }

    private void direction(double mx, double my, double px, double py, float anglediff) {
        double dx = mx - px;
        double dy = my - py;
        double alpha = Math.atan2(dy, dx);
        handler.angle = (float) Math.toDegrees(alpha);
        handler.angle += anglediff; //change for wider range of the rights bullet

        handler.angle = checkAngle(handler.angle); // get's overwritten if angle is not valid
        alpha = Math.toRadians(handler.angle); // overwrite angle
        velX = (float) (Math.cos(alpha) * bulletSpeed);
        velY = (float) (Math.sin(alpha) * bulletSpeed);

        setPos(px+Math.cos(alpha)*50-16, py+Math.sin(alpha)*50-16);
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
        this.x = (int) x;
        this.y = (int) y;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, 12, 12);
    }
}