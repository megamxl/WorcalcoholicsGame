package Woralcoholics.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Bullet extends GameObject {

    public static float bulletSpeed = 30;// 8;

    private final BufferedImage bullet_img;

    GameManager handler;

    public Bullet(int x, int y, ID id, GameManager handler, Animations an) {
        super(x, y, id, an);
        this.handler = handler;

        bullet_img = an.getImage(2, 3, 64, 64);
    }


    /**
     * map the mouse input to world coordinates
     *
     * @param mx
     * @param my
     * @param px
     * @param py
     */
    public void direction(double mx, double my, double px, double py) {
        double dx = mx - px;
        double dy = my - py;
        double alpha = Math.atan2(dy, dx);
        velX = (float) (Math.cos(alpha) * bulletSpeed);
        velY = (float) (Math.sin(alpha) * bulletSpeed);

    }

    public void directionRightbullet(double mx, double my, double px, double py) {
        double dx = mx - px;
        double dy = my - py;
        double alpha = Math.atan2(dy, dx);
        float angle = (float) Math.toDegrees(alpha);
        angle += 10; //change for wider range of the rights bullet

        angle = CheckAngle(angle); // get's overwritten if angle is not valid
        alpha = Math.toRadians(angle); // overwrite angle

        velX = (float) (Math.cos(alpha) * bulletSpeed);
        velY = (float) (Math.sin(alpha) * bulletSpeed);
    }

    public void directionLeftbullet(double mx, double my, double px, double py) {
        double dx = mx - px;
        double dy = my - py;
        double alpha = Math.atan2(dy, dx);
        float angle = (float) Math.toDegrees(alpha);
        angle -= 10; //change for wider range of the left bullet

        CheckAngle(angle);
        alpha = Math.toRadians(angle);


        velX = (float) (Math.cos(alpha) * bulletSpeed);
        velY = (float) (Math.sin(alpha) * bulletSpeed);
    }

    /**
     * if bullet colloids with block remove it
     */
    public void collision() {   //Collision Detection (Enemys, Blocks)
        for (int i = 0; i < handler.object.size(); i++) {
            GameObject tmpObject = handler.object.get(i);

            if (tmpObject.getId() == ID.Block) {
                if (this.getBounds().intersects(tmpObject.getBounds())) {
                    handler.removeObject(this);
                    //System.out.println("Collision");
                }
            }
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

    private float CheckAngle(float angle) {
        if (angle < 0) {
            angle += 360;
        }
        return angle;
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
        /*g.setColor(Color.WHITE);
        g.fillOval((int) x, (int) y, 8, 8);
        */
        g.drawImage(bullet_img, (int) x, (int) y, null);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, 12, 12);
    }

    public float getBulletSpeed() {
        return bulletSpeed;
    }

    public void setBulletSpeed(float bs) {
        bulletSpeed = bs;
    }
}

