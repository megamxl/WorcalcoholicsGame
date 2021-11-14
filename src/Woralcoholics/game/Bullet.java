package Woralcoholics.game;

import java.awt.*;

public class Bullet extends GameObject{

    private float bulletSpeed = 1;

    public Bullet(int x, int y, ID id) {
        super(x, y, id);
    }

    public void direction(double dx, double dy) {
        double d = Math.sqrt(Math.pow(dx,2) + Math.pow(dy,2));
        velX = (float) ((float) dx / d);
        velY = (float) ((float) dy / d);

        System.out.println(velX + " " + velY);
        if(dx == 500) {velX = 0;}
        if(dy == 282) {velY = 0;}
        /*if(dx < 500 && dy < 282) {
            velX *= -1;
            velY *= -1;
        }
        if(dx > 500 && dy < 282) {
            velY *= -1;
        }
        if(dx < 500 && dy > 282) {
            velY *= -1;
        }*/

        /*if(dx < 0) {velX *= -1;}
        if(dy < 0) {velY *= -1;}*/
    }

    @Override
    public void update() {
        x += velX;
        y += velY;

        if(x-4 > 1000 || y-4 > 563 || x < 0 || y < 0) {   //Out of Bounds
            handler.removeObject(this);
        }

        //Collision Detection
        for(int i = 0; i < handler.object.size(); i++ ) {
            GameObject tmpObject = handler.object.get(i);

            /*if (tmpObject.getId() == ID.Block || tmpObject.getId() == ID.Enemy) {
                handler.removeObject(this);
            }*/
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillOval(x, y, 8, 8);
    }

    @Override
    public Rectangle getBounds() {
        return null;
    }

    public float getBulletSpeed() {
        return bulletSpeed;
    }

    public void setBulletSpeed(float bs) {
        bulletSpeed = bs;
    }
}

