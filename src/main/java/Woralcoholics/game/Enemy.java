package Woralcoholics.game;

import javax.sound.sampled.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Enemy Class
 *
 * @author Maxlimilian Nowak
 * @author Christoph Oprawill
 */
public class Enemy extends GameObject {

    //region INSTANCE VARIABLES
    public final int upgradeAfterWave = 3;                  //After how many Waves an Upgrade should be granted
    public static float velocity = 0;
    public static int enemysAlive = 0;
    public static int waves = 1;
    public static int maxHp = 100;
    public static int ammoMax = 10;

    int choose = 0;
    int hp;
    protected float hpPercent = 1.0f;
    int low = -4;                                           //low and high values for different variations of enemy behaviour
    int high = 4;
    int booleanValue = 0;                                   //booleanvalue is for determining if enemy should charge player again or just running aimless around
    private boolean isAlive;
    public boolean isInGame;
    private Animations enemyAnimation;

    public boolean hittedWall = false;
    Integer[] solution = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
    //hittedWall is for changing the aiming target of player to nothing

    private GameManager manager;
    private EnemyShadow es;
    Random r = new Random();
    Score score;
    //endregion

    //region CONSTRUCTOR

    public Enemy(int x, int y, ID id, GameManager manager, ImageGetter an, Score score) {
        super(x, y, id, an);
        List<Integer> list = Arrays.asList(solution);
        Collections.shuffle(list);
        this.manager = manager;
        this.score = score;
        hp = maxHp;
        enemyAnimation = new Animations(9, Game.enemy[list.get(0)], Game.enemy[list.get(1)], Game.enemy[list.get(2)], Game.enemy[list.get(3)], Game.enemy[list.get(4)], Game.enemy[list.get(5)], Game.enemy[list.get(6)], Game.enemy[list.get(7)], Game.enemy[list.get(8)], Game.enemy[list.get(9)], Game.enemy[list.get(10)], Game.enemy[list.get(11)], Game.enemy[list.get(12)]);

    }

    //endregion

    //region PUBLIC METHODS

    public void update() {
        if (isInGame) {
            move();

            enemyAnimation.runAnimations();

            choose = r.nextInt(10);

            checkIfGone();

            collision();

            bulletCollision();

            isDead();
        }
    }

    public void render(Graphics g) {
        if (isInGame) {
            enemyAnimation.renderAnimation(g, (int) x, (int) y);
            if (hp != maxHp) {
                renderHPBar(g);
            }
        }
//        Graphics2D g2d = (Graphics2D) g;
//        g.setColor(Color.GREEN);
//        g2d.draw(topColider());
//        /*g2d.draw(leftColider());
//        *//*g2d.draw(rigthColider());*//*
//        g2d.draw(downColider());*/
//        g2d.draw(getBoundsAround());
//        //g.drawImage(enemy_img, (int) x, (int) y, null);
    }

    private Rectangle topColider() {
        Rectangle top = getBounds();
        top.width = 30;
        top.height = 5;
        top.y += 1;

        return top;
    }

    private Rectangle downColider() {
        Rectangle top = getBounds();
        top.width = 32;
        top.height = 5;

        top.y += 30;

        return top;
    }

    private Rectangle leftColider() {
        Rectangle top = getBounds();
        top.width = 5;
        top.x += 1;
        top.height = 30;
        return top;
    }

    private Rectangle rigthColider() {
        Rectangle top = getBounds();
        top.width = 5;
        top.height = 30;
        top.x += 30;
        top.x += 1;
        return top;
    }


    /***
     * Render function of the HP Bar
     * @param g Graphics from render
     */
    protected void renderHPBar(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect((int) x - 8, (int) y - 12, 48, 6);
        if (hpPercent > 0.5) {
            g.setColor(Color.green);
        } else if (hpPercent > 0.25) {
            g.setColor(Color.YELLOW);
        } else {
            g.setColor(Color.RED);
        }
        g.fillRect((int) x - 6, (int) y - 10, (int) (44.0 * hpPercent), 2);
        g.setColor(Color.WHITE);
        g.drawString(String.format("%.2f", hpPercent * 100) + "%", (int) (x - 2), (int) (y - 16));
    }

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

            if (tmpObject.getId() == ID.Block || tmpObject.getId() == ID.DestroyableBoxes) {

                if (getBoundsAround().intersects(tmpObject.getBounds())) {
                    // if it is colliding with wall it goes in the opposite direction
                    x += (velX * 3) * -1;
                    y += (velY * 3) * -1;
                    velX *= -1;
                    velY *= -1;

                    //enemy cant aim player anymore -> prevents stucking at the wall
                    hittedWall = true;

                    //moving between 4 and -4 per iterate
                    velX = r.nextInt(high - low) + low;
                    velY = r.nextInt(high - low) + low;
                }
            }


            // collision enemy with player
            if (tmpObject.getId() == ID.Player) {
                if (!hittedWall) {
                    // if hitted the wall enemy runs different like before
                    booleanValue = r.nextInt(100 - 1) + 1; //maybe change

                    if (booleanValue == 10) {
                        // now enemy aims to player
                        hittedWall = false;
                    }
                } else {
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

                }
            }

            if (tmpObject.getId() == ID.SwordHitbox) {
                Line2D line = new Line2D.Double(tmpObject.getX(), tmpObject.getY(),
                        tmpObject.getBounds().getWidth(), tmpObject.getBounds().getHeight());
                if (getBoundsAround().intersectsLine(line)) {
                    hp -= 110;
                    hpPercent = hp / (float) maxHp;
                    if (hp > 10) {
                        Game.UseEnemyHitmarker((int) x, (int) y);
                        playSoundEnemyHit();
                    }
                }
            }
        }
    }

    public void bulletCollision() {
        for (int i = 0; i < manager.bullets.size(); i++) {
            Bullet temp = manager.bullets.get(i);
            //if our bullet is colliding with the enemy hp get's -50
            if (getBounds().intersects(temp.getBounds()) && temp.inGame && temp.getId() == ID.Bullet) {
                //System.out.println("hit");
                hp -= 110;
                hpPercent = hp / (float) maxHp;
                if (hp > 10) {
                    Game.UseEnemyHitmarker((int) x, (int) y);
                    playSoundEnemyHit();
                }
                //System.out.println("einem enemy leben abgezogen " + hp);


                //System.out.println("es sind " + enemysAlive +" enemys am leben");
            }
        }
    }

    public void isDead() {
        if (hp <= 0) {
            if (this.getId() == ID.Enemy) {
                score.addScore(3);
                Game.money.addMoney(1);
            } else if (this.getId() == ID.GunnerEnemy) {
                score.addScore(10);
                Game.money.addMoney(2);
            }
            //System.out.println(score.showScore());

            int prob = Game.randomNumber(1, 5);
            remove();
            float curX = x;
            float curY = y;
            //int prob = 2;
            if (prob == 2) {
                Game.SpawnCreate((int) curX, (int) curY);
            }
        }
    }

    public static void Spawner(int waveSize, boolean solo, Random r) {
        if (solo) {                                         // if function gets passed false just spawn one enemy
            waveSize = 1;
        } else { // else increase the amount of the wave and calculate the amount of enemies
            //Game.SpawnGunnerEnemy();
            waveSize = (waveSize * 2) + 1;
        }

        // currently, capping ant 20 because of performance
        if (waveSize > 20) {
            waveSize = 20;
        }


        for (int i = 0; i < waveSize; i++) {                // spawner script
            // reset and declare x and y for every iteration
            int x = 0;
            int y = 0;
            // generate x an y random in range we use to load level
            x = r.nextInt(((55 * 32) - 1) - 1);
            y = r.nextInt(((55 * 32) - 1) - 1);

            Game.spawnEnemy(x, y);                          // spawns enemy
        }

    }

    /**
     * if an enemy gets out of bounce delete it and respawn it random
     */
    public void checkIfGone() {
        // previous ->  if ((y > 1054 || y < 64) || (x > 2000 || x < 0)) {
        if ((y > 1000 || y < 30) || (x > 2000 || x < 30)) {
            removeWithObject(this);
            Spawner(1, true, r);
            // System.out.println("GONE");
            //System.out.println("x:"+ x +" " + "y" + y);
        }
    }

    //endregion

    //region PRIVATE METHODS

    /**
     * Enemy gets removed
     */
    public void remove() {
        manager.removeObject(this);
        float curX = x;
        float curY = y;
        Game.UseEnemyShadow((int) curX, (int) curY);
        isInGame = false;

        playSoundEnemy();
        enemysAlive--;
        if (!Game.inTutorial) {
            if (enemysAlive <= 0) {
                maxHp += 50;
                waves++;
                if ((waves - 1) % upgradeAfterWave == 0) {
                    Game.startTimer(3, 2);  //after 3 secs, execute timerAction 2 -> change to UPGRADE_MENU
                } else {
                    Game.startTimer(5, 1);  //after 5 secs, execute timerAction 1 -> spawn next Wave
                    for (int i = 0; i < manager.object.size(); i++) {
                        if (manager.object.get(i).getId() == ID.Enemy || manager.object.get(i).getId() == ID.GunnerEnemy || manager.object.get(i).getId() == ID.EnemyBullet) {

                            GameObject tempObject = manager.object.get(i);   //update the camera position to stay focused on the player
                            tempObject = null;
                        }
                    }
                }
            }

        }
    }

    /**
     * needed if a specific enemy should get removed
     *
     * @param tempobject
     */
    public void removeWithObject(GameObject tempobject) {
        manager.removeObject(tempobject);
        tempobject = null;
        isInGame = false;
        enemysAlive--;
    }

    protected void playSoundEnemy() {
        try {
            new Thread(() -> {

                try {
                    manager.playSoundEnemyDead();
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
                    //System.out.println("ILLEGAL");
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void playSoundEnemyHit() {
        try {
            new Thread(() -> {

                try {
                    manager.playSoundEnemyHit();
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
                    //System.out.println("ILLEGAL");
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void spawnWaveAfterUpgrades() {
        Game.startTimer(5, 1); //after 5 secs, spawn the next wave
    }

    /**
     * check if enemy bounds with an Rectangle
     *
     * @return
     */
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, 32, 32);
    }

    public Rectangle getBoundsBig() {
        return new Rectangle((int) x -16, (int) y -16, 64, 64);
    }

    /**
     * check if enemy bounds with an Rectangle bigger than the actual enemy
     *
     * @return
     */
    public Rectangle getBoundsAround() {
        return new Rectangle((int) x, (int) y, 32, 32);
    }

    //endregion
}