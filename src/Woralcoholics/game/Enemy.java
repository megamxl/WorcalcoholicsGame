package Woralcoholics.game;

import javax.sound.sampled.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Enemy Class
 */
public class Enemy extends GameObject {

    private final BufferedImage enemy_img;


    //Handler is for collision detection
    private GameManager manager;
    Random r = new Random();
    int choose = 0;
    int hp = 100;
    public static boolean waited = false;

    /**
     * low and high values for different variations of enemy behaviour
     */
    int low = -4;
    int high = 4;
    /**
     * booleanvalue is for determining if enemy should charge player again or just running aimless around
     */
    int booleanvalue = 0;
    /**
     * hittedwall is for changing the aiming target of player to nothing
     */
    boolean hittedwall = false;
    Clip sound;

    static int enemysAlive = 0;
    static int waves = 1;

    public Enemy(int x, int y, ID id, GameManager manager, Animations an) {
        super(x, y, id, an);
        this.manager = manager;

        enemy_img = an.getImage(1, 4, 32, 32);
        enemysAlive++;
        //System.out.println("enemy created "+ enemysAlive );
    }

    public void move() {
        x += velX;
        y += velY;
    }

    /**
     * Collision enemy with a block | Collision enemy with player
     */
    public void collision() {
        for (int i = 0; i < manager.object.size(); i++) {

            GameObject tmpObject = manager.object.get(i);

            //if it's colliding with a block
            if (tmpObject.getId() == ID.Block) {
                if (getBoundsAround().intersects(tmpObject.getBounds())) {
                    // if it is colliding with wall it goes in the opposite direction
                    x += (velX * 5) * -1;
                    y += (velY * 5) * -1;
                    velX *= -1;
                    velY *= -1;
                    //enemy cant aim player anymore -> prevents stucking at the wall
                    hittedwall = true;

                    //moving between 4 and -4 per iterate
                    velX = r.nextInt(high - low) + low;
                    velY = r.nextInt(high - low) + low;
                }
            }

            //if our bullet is colliding with the enemy hp get's -50
            if (tmpObject.getId() == ID.Bullet) {
                if (getBounds().intersects(tmpObject.getBounds())) {
                    //System.out.println("hit");
                    hp -= 110;
                    removeWithObject(tmpObject);
                    if (enemysAlive <= 0) {
                        Game.TimerValue = 5;
                        Game.shouldTime = true;
                    }
                    //System.out.println("es sind " + enemysAlive +" enemys am leben");
                }

            }

            // collision enemy with player
            if (tmpObject.getId() == ID.Player) {

                if (hittedwall == false) {

                    // enemy behaviour
                    if (tmpObject.getX() + 6 >= x && tmpObject.getX() - 6 <= x) {
                        velX = 0;
                    } else if (tmpObject.getX() > x) {
                        velX = r.nextInt(5 - 0) + 0;

                    } else {

                        velX = r.nextInt(0 - (-5)) + (-5);

                    }
                    if (tmpObject.getY() + 6 >= y && tmpObject.getY() - 6 <= y) {
                        velY = 0;
                    } else if (tmpObject.getY() > y) {
                        velY = r.nextInt(5 - 0) + 0;

                    } else {

                        velY = r.nextInt(0 - (-5)) + (-5);

                    }

                } else {
                    // if hitted the wall enemy runs different like before
                    booleanvalue = r.nextInt(100 - 1) + 1; //maybe change

                    if (booleanvalue == 10) {
                        // now enemy aims to player
                        hittedwall = false;
                    }
                }
            }
        }
    }

    /**
     * Enemy gets removed
     */
    private void remove() {
        manager.removeObject(this);
        try {
            new Thread(() -> {

                try {
                    manager.playSound();
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

    private void removeWithObject(GameObject tempobject) {
        manager.removeObject(tempobject);
        enemysAlive--;
    }

    public void isDead() {
        if (hp <= 0) {
            remove();
        }
    }

    public void update() {
        move();

        choose = r.nextInt(10);

        checkIfGone();

        collision();

        isDead();

    }


    public void render(Graphics g) {
        g.drawImage(enemy_img, (int) x, (int) y, null);
    }

    /**
     * check if enemy bounds with an Rectangle
     * @return
     */
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, 32, 32);
    }

    /**
     * check if enemy bounds with an Rectangle bigger than the actual enemy
     * @return
     */
    public Rectangle getBoundsAround() {
        return new Rectangle((int) x, (int) y, 32, 32);
    }

    public static void Spawner(int Wavesize, boolean solo, Random r) {
        if (solo) {
            Wavesize = 1;
        } else {
            Wavesize = (Wavesize * 5) + 1;
        }

        for (int i = 0; i < Wavesize; i++) {
            int x = 0;
            int y = 0;
            x = r.nextInt(((55 * 32) - 1) - 1);
            y = r.nextInt(((55 * 32) - 1) - 1);

            for (int[] c : Game.wallCords) {
                if (c[0] == x || c[1] == y) {
                    //System.out.println("wall, this is i " + i);
                }
            }
            //System.out.println("spawning");
            Game.SpawnEnemy((int) x, (int) y);
        }
    }

    public void checkIfGone() {
        if ((y > 1054 || y < 64) || (x > 1900 || x < 0)) {
            removeWithObject(this);
            Spawner(1, true,r);
        }
    }


}

// method of playing sound

