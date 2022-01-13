package Woralcoholics.game;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * @author Maxlimilian Nowak
 * @author Christoph Oprawill
 */

public class GunnerEnemy extends Enemy {

    GameManager handler;

    private final BufferedImage gunner_enemy_img;       //Sprite of Gunner Enemy

    private float minDistanceToPlayer = 100;        //the minimal and maximum distances the Gunner should be away from the player
    private float maxDistanceToPlayer = 250;
    private double distanceToPlayer;
    private double bulletAlpha;       //angle that is needed for calculating the bullet path

    private final int checkFreeDistance = 32;
    public boolean inGame = false;
    public static float movementSpeed = 5;  //Movement-Speed of the Gunner


    private double px = 0, py = 0;    //Player Coordinates
    private double gx, gy;            //Gunner Coordinates

    private double wait = 3000 + System.currentTimeMillis();        //initial wait time for shooting Bullets
    private final double shootDel = 1500;   //Interval time at which the Gunner should shoot

    private enum state {    //All different states the Gunner can be in
        TOO_CLOSE,
        TOO_FAR,
        NEAR_WALL,
        WANDER
    }

    private state gunnerState;
    private state lastState;


    public GunnerEnemy(int x, int y, ID id, GameManager manager, ImageGetter an, Score score) {  //Constructor for Gunner Enemies
        super(x, y, id, manager, an, score);
        handler = manager;
        gx = getX();    //get Position of Gunner
        gy = getY();
        gunnerState = lastState = state.WANDER;
        int a = new Random().nextInt(360);  //set random initial movement
        velX = (float) (Math.cos(a*Math.PI/180) * movementSpeed);
        velY = (float) (Math.sin(a*Math.PI/180) * movementSpeed);
        gunner_enemy_img = an.getImage(1, 5, 64, 64);  //Get the Sprite for the Gunner from Spritesheet
    }

    /***
     * Update function of GunnerEnemy
     */
    public void update() {
        if (isInGame) {
            if(lastState != gunnerState) stateChange();     //state Change System similar to that in Game
            gx = getX();
            gy = getY();
            calcDistanceToPlayer();
            checkIfGone();
            behaviour();
            if (!Game.inTutorial) {
                move();
            }
            //move(); //FOR TESTING!!

            collision();    //Check if it's colliding with a Bullet or Block (Enemy.collision())
            bulletCollision();
            //Shoot after a delay
            double now = System.currentTimeMillis();
            if (now > wait) {
                if(los()) shoot();

                wait = now + shootDel;
            }
            isDead();   //Check if he is dead (Enemy.isDead())
        }
    }

    /***
     * Render function of GunnerEnemy
     */
    public void render(Graphics g) {
        /*g.setColor(Color.MAGENTA);
        g.fillRect((int)x +4, (int)y +2, 52, 60);
        g.setColor(Color.WHITE);


        g.drawLine((int)gx+32, (int)gy+32, (int)(gx+Math.cos(alpha)*(distanceToPlayer-50)), (int)(gy+Math.sin(alpha)*(distanceToPlayer-50)));*/

        if (isInGame) {
            g.drawImage(gunner_enemy_img, (int) x, (int) y, null);
            if (hp != maxHp) {
                renderHPBar(g);
            }
            g.drawRect((int)gx, (int)gy, 64, 64);
            g.drawLine((int)gx+32, (int)gy+32, (int)px, (int)py);
        }
    }

    private boolean los() {
        for(int i = 1; i*checkFreeDistance < distanceToPlayer; i++) {
            Point check = new Point((int) (Math.cos(bulletAlpha)*i*checkFreeDistance + gx),
                    (int) (Math.sin(bulletAlpha)*i*checkFreeDistance + gy));
            for(int j = 0; j < handler.object.size(); j++) {
                GameObject temp = handler.object.get(j);
                if(temp.getBounds().contains(check) && (temp.getId() == ID.Block || temp.getId() == ID.DestroyableBoxes)) {
                    return false;
                    //System.out.println("View obstructed by " + handler.object.get(j).getId());
                }
            }
            //System.out.println("Object " + i + ": " + check.x + " " + check.y);
            //System.out.println(bulletAlpha);
            //System.out.println("Player: " + px + " " + py);
        }
        return true;
    }

    private void stateChange() {
        lastState = gunnerState;
        switch(gunnerState) {
            case TOO_FAR -> {
                velX = (float) (Math.cos(bulletAlpha) * movementSpeed);
                velY = (float) (Math.sin(bulletAlpha) * movementSpeed);
            }
            case TOO_CLOSE -> {
                velX = (float) (Math.cos(bulletAlpha+Math.PI) * movementSpeed);
                velY = (float) (Math.sin(bulletAlpha+Math.PI) * movementSpeed);
            }
            case NEAR_WALL -> {
                x += -velX * 3;
                y += -velY * 3;
                velX *= -1;
                velY *= -1;
                System.out.println("NEAR WALL");
            }
            case WANDER -> {
                int a = new Random().nextInt(360);
                velX = (float) (Math.cos(a*Math.PI/180) * movementSpeed/5);
                velY = (float) (Math.sin(a*Math.PI/180) * movementSpeed/5);
            }
        }
    }

    /***
     * Function to calculate the Distance to the player, and act accordingly
     */
    private void calcDistanceToPlayer() {
        for (int i = 0; i < handler.object.size(); i++) {
            GameObject temp = handler.object.get(i);

            if (temp.getId() == ID.Player) {
                px = temp.getX() + 32;
                py = temp.getY() + 32;
                break;
            }
        }
        distanceToPlayer = Math.sqrt(Math.pow(px - gx, 2) + Math.pow(py - gy, 2));
        bulletAlpha = Math.atan2(py - gy, px - gx);

        gunnerState = state.WANDER;
        if(los()) {
            if (distanceToPlayer > maxDistanceToPlayer) {
                gunnerState = state.TOO_FAR;
            } else if (distanceToPlayer < minDistanceToPlayer) {
                gunnerState = state.TOO_CLOSE;
            }
        }
    }

    /***
     * How the Gunner should act depending on its state
     */
    public void behaviour() {
        switch (gunnerState) {

        }
        //System.out.println(gunnerState);
    }

    /***
     * Shoot a bullet
     */
    public void shoot() {
        gx += 16 - 4;
        gy += 16 - 4;
        for (int i = 0; i < handler.bullets.size(); i++) {
            Bullet temp = handler.bullets.get(i);
            if (!temp.inGame) {
                temp.setId(ID.EnemyBullet);
                temp.setPos(gx, gy);
                temp.direction(px, py, gx, gy, false, 0, true);
                playSoundGunnerEnemy();
                temp.inGame = true;
                break;
            }
        }
        //temp.direction(px, py, gx, gy, false, 0);     //Set the direction
        //handler.addObject(temp);

    }

    public void collision() {
        for (int i = 0; i < handler.object.size(); i++) {

            GameObject tmpObject = handler.object.get(i);

            if (tmpObject.getId() == ID.Block || tmpObject.getId() == ID.DestroyableBoxes) {
                if (getBoundsAround().intersects(tmpObject.getBounds())) {
                    gunnerState = state.NEAR_WALL;
                    break;
                }
            }
        }
    }

    @Override
    public void checkIfGone() {
        if ((y > 1000 || y < 30) || (x > 2000 || x < 30)) {
            removeWithObject(this);
            Game.spawnGunnerEnemy(500,500);
            // System.out.println("GONE");
            //System.out.println("x:"+ x +" " + "y" + y);
        }
    }

    private void playSoundGunnerEnemy() {
        try {
            new Thread(() -> {

                try {
                    handler.playSoundGunnerEnemy();
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                } catch (UnsupportedAudioFileException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    //e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x + 4, (int) y + 2, 52, 60);
    }

    @Override
    public Rectangle getBoundsAround() {
        return new Rectangle((int) gx, (int) gy, 74, 74);
    }

    /***
     * Some getter and setter functions
     */
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
