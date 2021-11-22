package Woralcoholics.game;

import java.awt.*;

public class GunnerEnemy extends Enemy {

    GameManager handler;

    private float minDistanceToPlayer = 100;
    private float maxDistanceToPlayer = 200;
    private float movementSpeed = 5;

    protected double px = 0, py = 0;
    protected double shootDel = 0;


    public GunnerEnemy(int x, int y, ID id, GameManager manager, Animations an) {
        super(x, y, id, manager, an);
        //velX = velY = 0;
        handler = manager;
    }

    public void behaviour() {
        double gx = this.getX();
        double gy = this.getY();
        for(int i = 0; i < handler.object.size(); i++) {
            GameObject temp = handler.object.get(i);

            if(temp.getId() == ID.Player) {
                px = temp.getX()+16;
                py = temp.getY()+24;
                break;
            }
        }
        /*double distanceToPlayer = Math.sqrt(Math.pow(px-gx, 2) + Math.pow(py-gy, 2));
        double alpha = Math.atan2(py-gy, px-gx);
        if(distanceToPlayer > maxDistanceToPlayer) {
            velX = (float) (Math.cos(alpha) * movementSpeed);
            velY = (float) (Math.sin(alpha) * movementSpeed);
        }
        if(distanceToPlayer < minDistanceToPlayer) {
            alpha += Math.PI;
            velX = (float) (Math.cos(alpha) * movementSpeed);
            velY = (float) (Math.sin(alpha) * movementSpeed);
        }
        else
            velX = velY = 0;*/
    }

    public void shoot() {
        double now = System.currentTimeMillis();
        if(now > shootDel) {
            double gx = this.getX()+16;
            double gy = this.getY()+16;
            Bullet temp = new Bullet((int) gx-4, (int) gy-4, ID.Bullet, handler, an);
            temp.direction(px, py, gx, gy);
            handler.addObject(temp);
            shootDel += 500;
        }
    }

    public void update() {
        behaviour();
        move();
        collision();
        shoot();
        isDead();
    }

    public void render(Graphics g) {
        g.setColor(Color.MAGENTA);
        g.fillRect((int)x, (int)y, 32, 32);
    }

    public float getMinDistanceToPlayer() {
        return minDistanceToPlayer;
    }

    public void setMinDistanceToPlayer(float d) {
        minDistanceToPlayer = d;
    }

    public float getMaxDistanceToPlayer() {
        return maxDistanceToPlayer;
    }

    public void setMaxDistanceToPlayer(float d) {
        maxDistanceToPlayer = d;
    }

    public float getMovementSpeed() {
        return movementSpeed;
    }

    public void setMovementSpeed(float m) {
        movementSpeed = m;
    }
}
