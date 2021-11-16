package Woralcoholics.game;

import java.awt.*;
import java.util.Vector;

public class Bullet extends GameObject{

    private float bulletSpeed = 5;

    public Bullet(int x, int y, ID id) {
        super(x, y, id);
    }


    public void direction(double mx, double my, double px, double py) {
        //double c = Math.sqrt(Math.pow(mx,2) + Math.pow(my,2));
        double dx = mx-px;
        double dy = my-py;
        double alpha = Math.atan2(dy, dx);
        //System.out.println(alpha);
        velX = (float) (Math.cos(alpha) * bulletSpeed);
        velY = (float) (Math.sin(alpha) * bulletSpeed);
        /*
        velX = (float) (bulletSpeed * mx/c);
        velY = (float) (bulletSpeed * my/c);*/

        //System.out.println(velX + " " + velY);
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

            if (tmpObject.getId() == ID.Block || tmpObject.getId() == ID.Enemy) {
                handler.removeObject(this);
            }
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillOval((int)x, (int)y, 8, 8);
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

