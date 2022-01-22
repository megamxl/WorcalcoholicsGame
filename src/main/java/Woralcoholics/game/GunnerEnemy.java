package Woralcoholics.game;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * @author Lukas Schelepet
 * @author Maxlimilian Nowak
 * @author Christoph Oprawill
 */

public class GunnerEnemy extends Enemy {

    GameManager handler;

    private final BufferedImage gunner_enemy_img;       //Sprite of Gunner Enemy

    private float minDistanceToPlayer = 200;        //the minimal and maximum distances the Gunner should be away from the player
    private float maxDistanceToPlayer = 250;
    private double distanceToPlayer;
    private double alpha;       //angle that is needed for calculating the bullet path

    private final int checkFreeDistance = 32;   //base distance in which los() should check for obstacles
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
            if (!Game.inTutorial) {
                move();
            }
            //move(); //FOR TESTING!!

            collision();    //Check if it's colliding with an Obstacle
            bulletCollision();  //check if it's colliding with a bullet
            //Shoot after a delay
            double now = System.currentTimeMillis();
            if (now > wait) {
                if(los()) shoot();  //if the player is in line of sight, shoot
                wait = now + shootDel;
            }
            isDead();   //Check if he is dead (Enemy.isDead())
        }
    }

    /***
     * Render function of GunnerEnemy
     */
    public void render(Graphics g) {
        if (isInGame) {
            g.drawImage(gunner_enemy_img, (int) x, (int) y, null);
            if (hp != maxHp) {
                renderHPBar(g);     //render the HP bar, if it was hit at least once
            }
        }
    }

    /***
     * Line of Sight calculation function
     */
    private boolean los() {
        for(int i = 1; i*checkFreeDistance < distanceToPlayer; i++) {   //for every tuple of the base distance until we reach the player...
            Point checkpoint = new Point((int) (Math.cos(alpha)*i*checkFreeDistance + gx),
                    (int) (Math.sin(alpha)*i*checkFreeDistance + gy));
            for(int j = 0; j < handler.object.size(); j++) {    //...check if any Object...
                GameObject temp = handler.object.get(j);
                //...contains the Point "checkpoint"
                if(temp.getBounds().contains(checkpoint) && (temp.getId() == ID.Block || temp.getId() == ID.DestroyableBoxes)) {
                    return false;   //if there is an Object at any Point in the los, return false
                }
            }
        }
        return true;    //if not, return true
    }

    /***
     * StateChange function of GunnerEnemy
     */
    private void stateChange() {
        lastState = gunnerState;
        switch(gunnerState) {
            case TOO_FAR -> {   //get closer to the Player
                velX = (float) (Math.cos(alpha) * movementSpeed);
                velY = (float) (Math.sin(alpha) * movementSpeed);
            }
            case TOO_CLOSE -> {     //get farther away from the Player
                velX = (float) (Math.cos(alpha +Math.PI) * movementSpeed);
                velY = (float) (Math.sin(alpha +Math.PI) * movementSpeed);
            }
            case NEAR_WALL -> {     //reverse from the wall
                x += -velX * 3;
                y += -velY * 3;
                velX *= -1;
                velY *= -1;
            }
            case WANDER -> {    //wander around randomly
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
        //get the position of the Player
        for (int i = 0; i < handler.object.size(); i++) {
            GameObject temp = handler.object.get(i);

            if (temp.getId() == ID.Player) {
                px = temp.getX() + 32;
                py = temp.getY() + 32;
                break;
            }
        }

        distanceToPlayer = Math.sqrt(Math.pow(px - gx, 2) + Math.pow(py - gy, 2));  //calculate the distance with Pythagoras
        alpha = Math.atan2(py - gy, px - gx);

        gunnerState = state.WANDER; //set state to WANDER, unless...
        if(los()) {     //the Player is in los
            if (distanceToPlayer > maxDistanceToPlayer) {
                gunnerState = state.TOO_FAR;
            } else if (distanceToPlayer < minDistanceToPlayer) {
                gunnerState = state.TOO_CLOSE;
            }
        }
    }

    /***
     * Shoot a bullet
     */
    public void shoot() {
        gx += 16 - 4;   //adjust coordinates of the bullet
        gy += 16 - 4;
        //grab a free bullet from the bullet pool
        for (int i = 0; i < handler.bullets.size(); i++) {
            Bullet temp = handler.bullets.get(i);
            if (!temp.inGame) {
                temp.setId(ID.EnemyBullet); //change its ID to be able to hurt the Player
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

    /***
     * Collision detection function
     */
    public void collision() {
        for (int i = 0; i < handler.object.size(); i++) {
            GameObject tmpObject = handler.object.get(i);

            //if the GunnerEnemy collides with a Block or Box, change to the NEAR_WALL state
            if (tmpObject.getId() == ID.Block || tmpObject.getId() == ID.DestroyableBoxes) {
                if (getBoundsAround().intersects(tmpObject.getBounds())) {
                    gunnerState = state.NEAR_WALL;
                    break;
                }
            }
            if(tmpObject.getId() == ID.SwordHitbox) {
                Line2D line = new Line2D.Double(tmpObject.getX(), tmpObject.getY(),
                        tmpObject.getBounds().getWidth(), tmpObject.getBounds().getHeight());
                if(getBoundsAround().intersectsLine(line)) {
                    hp -= 110;
                    hpPercent = hp/(float)maxHp;
                    if (hp > 10) playSoundEnemyHit();
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

    /***
     * Playing Sounds of the GunnerEnemy
     */
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
}