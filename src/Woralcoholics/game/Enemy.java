package Woralcoholics.game;

import javax.sound.sampled.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

/**
 * Enemy Class
 */
public class Enemy<privare> extends GameObject {

    //region INSTANCE VARIABLES
    private final BufferedImage enemy_img;
    public static float velocity = 0;
    private GameManager manager;
    int choose = 0;
    int hp = 100;
    Random r = new Random();
    Score score;
    int low = -4; //low and high values for different variations of enemy behaviour
    int high = 4;
    int booleanvalue = 0; //booleanvalue is for determining if enemy should charge player again or just running aimless around


    boolean hittedwall = false; //hittedwall is for changing the aiming target of player to nothing
    static int enemysAlive = 0;
    static int waves = 1;
    final int upgradeAfterWave = 3;     //After how many Waves an Upgrade should be granted
    //endregion

    //region CONSTRUCTOR
    public Enemy(int x, int y, ID id, GameManager manager, Animations an, Score score) {
        super(x, y, id, an);
        this.manager = manager;
        this.score = score;

        enemy_img = an.getImage(1, 4, 32, 32);
        enemysAlive++;
        //System.out.println("enemy created "+ enemysAlive );
    }
    //endregion

    //region PUBLIC METHODS
    /***
     * x value of enemy gets changed by vel Values
     */
    public void move() {
        x += velX + velocity;
        y += velY + velocity;
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

                    if(this.getId() == ID.Enemy){score.addScore(3);}
                    else if(this.getId() == ID.GunnerEnemy){score.addScore(10);}
                    //System.out.println(score.showScore());

                    removeWithObject(tmpObject);
                    if(!Game.inTutorial){
                        if (enemysAlive <= 0) {
                            waves++;
                            if ((waves - 1) % upgradeAfterWave == 0) {
                                Game.TimerValue = 0;    //0 secs (actually just to unrender the last enemy and bullet)
                                Game.shouldTime = true; //activate Timer
                                Game.timerAction = 2;   //execute timerAction 2 -> wait a bit
                            } else {
                                Game.TimerValue = 5;    //5 secs to spawn next wave
                                Game.shouldTime = true; //activate Timer
                                Game.timerAction = 1;   //execute timerAction 1 -> spawn next Wave
                            }
                    }

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


    public void isDead() {
        if (hp <= 0) {
            int prob = Game.randomNumber(1,4);
            remove();
            float curX = x;
            float curY = y;
            //int prob = 2;
            if(prob == 2){
                Game.SpawnCreate((int)curX,(int)curY);
            }

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
     *
     * @return
     */
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, 32, 32);
    }

    /**
     * check if enemy bounds with an Rectangle bigger than the actual enemy
     *
     * @return
     */
    public Rectangle getBoundsAround() {
        return new Rectangle((int) x, (int) y, 32, 32);
    }

    public static void Spawner(int Wavesize, boolean solo, Random r) {
        if (solo) {                                         // if function gets passed false just spawn one enemy
            Wavesize = 1;
        } else {                                            // else increase the amount of the wave and calculate the amount of enemies
            Game.SpawnGunnerEnemy();
            Wavesize = (Wavesize * 2) + 1;
        }

        for (int i = 0; i < Wavesize; i++) {                // spawner script
            // reset and declare x and y for every iteration
            int x = 0;
            int y = 0;
            // generate x an y random in range we use to load level
            x = r.nextInt(((55 * 32) - 1) - 1);
            y = r.nextInt(((55 * 32) - 1) - 1);

            Game.SpawnEnemy(x, y);                          // spawns enemy
        }

    }

    /**
     * if an enemy gets out of bounce delete it and respawn it random
     */
    public void checkIfGone() {
        if ((y > 1054 || y < 64) || (x > 1900 || x < 0)) {
            removeWithObject(this);
            Spawner(1, true, r);
        }
    }

    //endregion

    //region PRIVATE METHODS
    /**
     * Enemy gets removed
     */
    private void remove() {
        manager.removeObject(this);
        playSoundEnemy();

    }

    /**
     * needed if a specific enemy should get removed
     * @param tempobject
     */
    private void removeWithObject(GameObject tempobject) {
        manager.removeObject(tempobject);
        enemysAlive--;
    }
    private void playSoundEnemy() {
        try {
            new Thread(() -> {

                try {
                    manager.playSoundEnemy();
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
    //endregion
}


