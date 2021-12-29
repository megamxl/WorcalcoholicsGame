package Woralcoholics.game;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GunnerEnemy extends Enemy {

    GameManager handler;

    private final BufferedImage gunner_enemy_img;       //Sprite of Gunner Enemy

    private float minDistanceToPlayer = 250;        //the minimal and maximum distances the Gunner should be away from the player
    private float maxDistanceToPlayer = 500;
    private double distanceToPlayer;
    private double alpha;       //angle that is needed for calculating the bullet path

    private final int checkFreeDistance = 32+50;

    public static float movementSpeed = 5;  //Movement-Speed of the Gunner

    private double px = 0, py = 0;    //Player Coordinates
    private double gx, gy;            //Gunner Coordinates

    private double wait = 3000 + System.currentTimeMillis();        //initial wait time for shooting Bullets
    private final double shootDel = 1500;   //Interval time at which the Gunner should shoot

    private enum state {    //All different states the Gunner can be in
        TOO_CLOSE,
        TOO_FAR,
        NEAR_WALL,
        STAY
    }
    private state gunnerState;


    public GunnerEnemy(int x, int y, ID id, GameManager manager, Animations an, Score score) {  //Constructor for Gunner Enemies
        super(x, y, id, manager, an, score);
        handler = manager;
        gx = getX();    //get Position of Gunner
        gy = getY();
        gunner_enemy_img = an.getImage(1,5,64,64);  //Get the Sprite for the Gunner from Spritesheet
    }

    /***
     * Function to calculate the Distance to the player, and act accordingly
     */
    private void calcDistanceToPlayer() {
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

    /***
     * How the Gunner should act depending on its state
     */
    public void behaviour() {
        switch(gunnerState) {
            case TOO_FAR -> {   //-> move closer to the player
                velX = (float) (Math.cos(alpha) * movementSpeed);
                velY = (float) (Math.sin(alpha) * movementSpeed);
            }
            case TOO_CLOSE -> {
                alpha += Math.PI;   //-> move away from the player
                velX = (float) (Math.cos(alpha) * movementSpeed);
                velY = (float) (Math.sin(alpha) * movementSpeed);
            }
            case NEAR_WALL -> {
                //alpha += checkIfFree();
            }
            case STAY -> velX = velY = 0;   //stay still
        }
        //System.out.println(gunnerState);
    }

    /***
     * Shoot a bullet
     */
    public void shoot() {
        gx += 16;
        gy += 16;
        EnemyBullet temp = new EnemyBullet((int) gx-4, (int) gy-4, ID.EnemyBullet, handler, an);    //Create a new bullet, that can harm the player
        temp.direction(px, py, gx, gy);     //Set the direction
        handler.addObject(temp);
        playSoundGunnerEnemy();
    }

    private void checkIfFree() {
    }

    private void playSoundGunnerEnemy()
    {
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
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * Update function of GunnerEnemy
     */
    public void update() {
        gx = getX();
        gy = getY();
        calcDistanceToPlayer();
        behaviour();
        move();
        collision();    //Check if it's colliding with a Bullet or Block (Enemy.collision())
        //checkIfFree();
        //Shoot after a delay
        double now = System.currentTimeMillis();
        if(now > wait) {
            shoot();
            wait = now + shootDel;
        }
        isDead();   //Check if he is dead (Enemy.isDead())
    }

    /***
     * Render function of GunnerEnemy
     */
    public void render(Graphics g) {
        /*g.setColor(Color.MAGENTA);
        g.fillRect((int)x +4, (int)y +2, 52, 60);
        g.setColor(Color.WHITE);
        g.drawRect((int)gx+2, (int)gy, 64, 64);
        g.drawLine((int)gx+32, (int)gy+32, (int)px, (int)py);
        g.drawLine((int)gx+32, (int)gy+32, (int)(gx+Math.cos(alpha)*(distanceToPlayer-50)), (int)(gy+Math.sin(alpha)*(distanceToPlayer-50)));*/
        g.drawImage(gunner_enemy_img,(int)x,(int)y, null);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int)x +4, (int)y +2, 52, 60);
    }

    @Override
    public Rectangle getBoundsAround() {
        return new Rectangle((int)gx, (int)gy, 50, 50);
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
