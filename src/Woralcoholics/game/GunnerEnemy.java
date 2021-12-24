package Woralcoholics.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GunnerEnemy extends Enemy {

    GameManager handler;

    private final BufferedImage gunner_enemy_img;

    private float minDistanceToPlayer = 250;
    private float maxDistanceToPlayer = 500;
    private double distanceToPlayer;
    private double alpha;

    private float movementSpeed = 5;

    protected double px = 0, py = 0;

    private double wait = 0;
    private final double shootDel = 1500;

    private enum state {
        TOO_CLOSE,
        TOO_FAR,
        STAY
    }

    private state gunnerState;


    public GunnerEnemy(int x, int y, ID id, GameManager manager, Animations an) {
        super(x, y, id, manager, an);
        handler = manager;

        gunner_enemy_img = an.getImage(1,5,64,64);
    }

    private void calcDistanceToPlayer() {
        double gx = this.getX();
        double gy = this.getY();
        for(int i = 0; i < handler.object.size(); i++) {
            GameObject temp = handler.object.get(i);

            if(temp.getId() == ID.Player) {
                px = temp.getX()+32;
                py = temp.getY()+32;
                break;
            }
        }
        distanceToPlayer = Math.sqrt(Math.pow(px-gx, 2) + Math.pow(py-gy, 2));
        alpha = Math.atan2(py-gy, px-gx);

        if(distanceToPlayer > maxDistanceToPlayer) {
            gunnerState = state.TOO_FAR;
        }
        else if(distanceToPlayer < minDistanceToPlayer) {
            gunnerState = state.TOO_CLOSE;
        }
        else
            gunnerState = state.STAY;
    }

    public void behaviour() {
        switch(gunnerState) {
            case TOO_FAR -> {
                velX = (float) (Math.cos(alpha) * movementSpeed);
                velY = (float) (Math.sin(alpha) * movementSpeed);
            }
            case TOO_CLOSE -> {
                alpha += Math.PI;
                velX = (float) (Math.cos(alpha) * movementSpeed);
                velY = (float) (Math.sin(alpha) * movementSpeed);
            }
            case STAY -> velX = velY = 0;
        }
        //System.out.println(gunnerState);
    }

    public void shoot() {
        double gx = this.getX()+16;
        double gy = this.getY()+16;
        EnemyBullet temp = new EnemyBullet((int) gx-4, (int) gy-4, ID.EnemyBullet, handler, an);
        temp.direction(px, py, gx, gy);
        handler.addObject(temp);
    }

    public boolean los(GameObject o) {
        return true;
    }

    public void update() {
        calcDistanceToPlayer();
        behaviour();
        move();
        collision();
        double now = System.currentTimeMillis();
        if(now > wait) {
            shoot();
            wait = now + shootDel;
        }
        isDead();
    }

    public void render(Graphics g) {
       /* g.setColor(Color.MAGENTA);
        g.fillRect((int)x +8, (int)y +2, 52, 60);*/
        g.drawImage(gunner_enemy_img,(int)x,(int)y, null);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int)x +8, (int)y +2, 52, 60);
    }

    @Override
    public Rectangle getBoundsAround() {
        return new Rectangle((int)x +8, (int)y +2, 52, 60);
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
