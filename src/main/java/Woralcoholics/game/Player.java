package Woralcoholics.game;

import javax.sound.sampled.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Inspiration https://www.youtube.com/watch?v=vKomghrsniU
 *
 * @author Maxlimilian Nowak
 * @author Christoph Oprawill
 */

public class Player extends GameObject {

    GameManager handler;
    Game game;
    Camera cam;
    Upgrades upgrades; //use upgrades.method for upgrade changes in Game and Player class

    public static Boolean takesDamage;

    private final BufferedImage player_img;
    private BufferedImage player_gun_img;
    private Animations playerWalkLeft;
    private Animations playerWalkRigth;
    private Animations playerIdle;

    private double invincibleTime = 1000;

    private float diagonalMultiplier = 1;
    private Boolean movingVertical = false;
    private Boolean movingHorizontal = false;
    private Boolean doOnceForZoneOne = false;
    private Boolean doOnceForZoneTow = false;
    private Boolean doOnceForZoneThree = false;
    Thread backgroundThread;
    // better method would be to wait until the thread is finished and then start the new sound

    public Player(int x, int y, ID id, GameManager GameManager, Game game, Camera cam, ImageGetter an) {
        super(x, y, id, an);
        this.handler = GameManager;
        this.game = game;
        this.cam = cam;
        this.upgrades = new Upgrades(game);

        playerWalkLeft = new Animations(3, Game.playerWalkingLeft[0], Game.playerWalkingLeft[1], Game.playerWalkingLeft[2], Game.playerWalkingLeft[3], Game.playerWalkingLeft[4], Game.playerWalkingLeft[5], Game.playerWalkingLeft[6], Game.playerWalkingLeft[7], Game.playerWalkingLeft[8], Game.playerWalkingLeft[9]);
        playerWalkRigth = new Animations(3, Game.playerWalkingRight[0], Game.playerWalkingRight[1], Game.playerWalkingRight[2], Game.playerWalkingRight[3], Game.playerWalkingRight[4], Game.playerWalkingRight[5], Game.playerWalkingRight[6], Game.playerWalkingRight[7], Game.playerWalkingRight[8], Game.playerWalkingRight[9]);
        playerIdle = new Animations(3, Game.playerIdle[0], Game.playerIdle[1], Game.playerIdle[2], Game.playerIdle[3]);

        player_img = an.getImage(1, 3, 64, 64);
        player_gun_img = an.getImage(5, 10, 64, 64);

    }

    @Override
    public void update() {
        x += velX;
        y += velY;

        playerWalkLeft.runAnimations();
        playerWalkRigth.runAnimations();
        playerIdle.runAnimations();

        checkIfGone();

        collision();
        isDead();
        keySounds();
        movement();
        checkGunRenderStatus();

        if (Game.inTutorial) {
            tutorial();
        }
        //System.out.println(x + " " + y);
    }


    @Override
    public void render(Graphics g) {
        /*Graphics2D g2d = (Graphics2D) g;
        g.setColor(Color.GREEN);
        g2d.draw(getBounds());*/
        if (velX < 0) {
            playerWalkLeft.renderAnimation(g, (int) x, (int) y, 64, 64);
        } else if (velX > 0 || velY > 0 || velY < 0) {
            playerWalkRigth.renderAnimation(g, (int) x, (int) y, 64, 64);
        } else {
            playerIdle.renderAnimation(g, (int) x, (int) y, 64, 64);
        }
        g.drawImage(player_gun_img, (int) x + 42, (int) y + 25, null); // x and y adjustable for gun position

        // draw other colliders
        /*g.setColor(Color.RED);
        g2d.draw(getBoundsX());
        g.setColor(Color.RED);
        g2d.draw(getBoundsY());*/
    }

    @Override
    public Rectangle getBounds() {
        //return new Rectangle((int) x + 12, (int) y, 40, 62);
        return new Rectangle((int) x + 13, (int) y, 38, 62);
    }

    public Rectangle getBoundsX() {
        Rectangle tempX = getBounds();
        tempX.width += 9.4;
        tempX.x -= 4.5f;
        return tempX;
    }

    public Rectangle getBoundsY() {
        Rectangle tempY = getBounds();
        tempY.height += 8;
        tempY.y -= 4f;
        return tempY;
    }

    /**
     * if player goes out of bounce set him to the spawn point
     */
    private void checkIfGone() {
        if ((y > 1054 || y < 32) || (x > 2000 || x < 0)) {
            x = Game.PlayerX;
            y = Game.PlayerY;
        }
    }

    private void checkGunRenderStatus() {
        int[] colrow = new int[2];
        colrow = game.getColRowFromIndex();
        player_gun_img = an.getImage(colrow[0], colrow[1], 64, 64);

        final double rads = Math.toRadians(handler.angle);
        final double sin = Math.abs(Math.sin(rads));
        final double cos = Math.abs(Math.cos(rads));
        final int w = (int) Math.floor(player_gun_img.getWidth() * cos + player_gun_img.getHeight() * sin);
        final int h = (int) Math.floor(player_gun_img.getHeight() * cos + player_gun_img.getWidth() * sin);
        final BufferedImage rotatedImage = new BufferedImage(w, h, player_gun_img.getType());
        final AffineTransform at = new AffineTransform();
        at.translate(w / 2, h / 2);
        at.rotate(rads,0, 0);
        at.translate(-player_gun_img.getWidth() / 2, -player_gun_img.getHeight() / 2);
        final AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        rotateOp.filter(player_gun_img,rotatedImage);
        player_gun_img = rotatedImage;
    }



    /**
     * Collision Detection function for the Player
     */
    private void collision() {
        for (int i = 0; i < handler.object.size(); i++) {
            GameObject tempobject = handler.object.get(i);

            // --------------------------------------------------------------
            if (getBoundsX().intersects((tempobject.getBounds()))) {
                ID tempID = tempobject.getId();
                switch (tempID) {
                    case Block, DestroyableBoxes -> {
                        //System.out.println("X");
                        x += velX * -1;
                    }
                }
            }
            // --------------------------------------------------------------
            if (getBoundsY().intersects((tempobject.getBounds()))) {
                ID tempID = tempobject.getId();
                switch (tempID) {
                    case Block, DestroyableBoxes -> {
                        //System.out.println("Y");
                        y += velY * -1;
                    }
                }
            }
            // --------------------------------------------------------------
            if (getBounds().intersects((tempobject.getBounds()))) {  //If player collides with another object...
                ID tempID = tempobject.getId();     //...get ID of said object...
                switch (tempID) {         //...and determine what should happen
                    case Block, DestroyableBoxes -> {
                        // check for block coordinate to see where it is located in relation to the player
                        // or make it dependent on key pressed?
                        x += velX * -1;
                        y += velY * -1;
                    }
                    case Crate -> {
                        upgrades.setMunition(upgrades.getMunition() + 10);
                        if (upgrades.getMunition() > 49) {
                            upgrades.setMunition(50);
                        }
                        handler.ammo = true;
                        handler.removeObject(tempobject);
                        playSoundAmmoReload();
                    }

                    case Enemy/*, EnemyBullet*/ -> {
                        playerSoundHurt();
                        // Implement Bloodscreen
                        if (game.hp > 0) {    //if player has health and is not invincible
                            switch (tempID) {
                                /*case EnemyBullet -> {
                                    handler.removeObject(tempobject);   //Remove Enemy Bullet if Player is hit
                                    upgrades.damaged(20);
                                }*/
                                case Enemy -> {
                                    if (handler.enemy.contains(tempobject)) { //enemy is on player
                                        invincibleTime++;
                                        //System.out.println(invincibleTime);
                                        if (invincibleTime % 30 == 0) { // %30 for how fast the HP is reducing
                                            upgrades.damaged(10);
                                        }
                                    } else {  //enemy is attacking player from a distance
                                        upgrades.damaged(10);
                                        handler.enemy.add(tempobject);
                                        //System.out.println(handler.enemy.size() + String.valueOf(tempobject));
                                    }
                                }
                            }
                            if (!Game.takesDamage) {
                                Game.takesDamage = true;
                                Game.startTimer(1, 5);
                            }
                            cam.shake = true;
                            //wait = now + invincibleTime;
                            // sound
                        }
                    }
                }
            } else {
                if (handler.enemy.contains(tempobject)) {
                    handler.enemy.remove(tempobject);
                    //System.out.println(handler.enemy.size() + String.valueOf(tempobject));
                } else {

                }
            }
        }
        for (int i = 0; i < handler.bullets.size(); i++) {
            Bullet temp = handler.bullets.get(i);
            if (getBounds().intersects(temp.getBounds()) && temp.inGame && temp.getId() == ID.EnemyBullet) {
                upgrades.damaged(20);
                temp.setId(ID.Bullet);
                temp.inGame = false;
                temp.setPos(0, 0);
            }
        }
    }

    private void isDead() {
        if (game.hp <= 0) {
            playSoundGameOver();
            Game.setState(GameState.GAME_OVER);         //if the player has no HP left, its GAME OVER
            Game.loaded = false;
        }
    }

    private void keySounds() {
        if (handler.isL()) {
            try {
                handler.soundv = 2;
                handler.backgroundsound.close();
                playBackgroundSound();
            } catch (Exception ex) {
            }
        }
        if (handler.isK()) {
            try {
                handler.soundv = 1;
                handler.backgroundsound.close();
                playBackgroundSound();
            } catch (Exception ex) {
            }
        }
        if (handler.isM()) {
            try {
                handler.soundv = 0;
                //handler.backgroundsound.close(); -> just Sound Effects get muted
                //playBackgroundSound();
            } catch (Exception ex) {
            }
        }
    }

    private void movement() {
        if (movingVertical && movingHorizontal) {
            diagonalMultiplier = 0.75f;
        } else {
            diagonalMultiplier = 1;
        }

        // Vertical Movement
        if (handler.isUp() && !handler.isDown()) {
            velY = -5 * diagonalMultiplier;
            movingVertical = true;
            //playerMovementSound();
        }
        if (handler.isDown() && !handler.isUp()) {
            velY = 5 * diagonalMultiplier;
            movingVertical = true;
            //playerMovementSound();
        }
        if ((handler.isUp() && handler.isDown()) || !handler.isUp() && !handler.isDown()) {
            velY = 0;
            movingVertical = false;
        }
        // Horizontal Movement
        if (handler.isRight() && !handler.isLeft()) {
            velX = 5 * diagonalMultiplier;
            movingHorizontal = true;
            //playerMovementSound();
        }
        if (handler.isLeft() && !handler.isRight()) {
            velX = -5 * diagonalMultiplier;
            movingHorizontal = true;
            //playerMovementSound();
        }
        if ((handler.isLeft() && handler.isRight()) || (!handler.isLeft() && !handler.isRight())) {
            velX = 0;
            movingHorizontal = false;
        }
    }

    private void tutorial() {
        if (x > 450 && x < 548 && y > 615 && y < 798) {
            Game.inFirstTutorialZone = true;
        }
        if (Game.inFirstTutorialZone) {
            if (!doOnceForZoneOne) {
                Game.SpawnEnemy(647, 208);
                Game.SpawnEnemy(900, 325);
                doOnceForZoneOne = true;
            }
        }
        if (x > 920 && x < 1105 && y > 50 && y < 105) {
            Game.inSecondTutorialZone = true;
        }
        if (Game.inSecondTutorialZone) {
            if (!doOnceForZoneTow) {
                System.out.println("in second");
                Game.SpawnGunnerEnemyWithCords(1293, 534);
                Game.SpawnCreate(1809, 329);
                doOnceForZoneTow = true;
            }
        }
        if (x > 1624 && x < 1959 && y < 123) {
            Game.currentState = GameState.MAIN_MENU;
            Game.inTutorial = false;
        }
    }

    // region SOUND

    /***
     * Runs the sound if player gets hurt
     */
    private void playerSoundHurt() {
        try {
            new Thread(() -> {

                try {
                    handler.playSoundPlayerHurt();
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                } catch (UnsupportedAudioFileException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    // e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * Runs the sound if crate is collected
     */
    private void playSoundAmmoReload() {
        try {
            new Thread(() -> {

                try {
                    handler.playSoundAmmoReload();
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                } catch (UnsupportedAudioFileException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    //e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playSoundGameOver() {
        try {
            new Thread(() -> {
                try {
                    handler.playSoundGameOver();
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                } catch (UnsupportedAudioFileException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * Runs the sound if player moves
     */
    private void playerMovementSound() {
        try {
            new Thread(() -> {

                try {
                    handler.playerMovementSound();
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                } catch (UnsupportedAudioFileException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    // e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * Function to run backgroundsound
     */
    private void playBackgroundSound() {
        backgroundThread = new Thread(() -> {
            try {
                handler.playBackgroundSound();
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
        });
        backgroundThread.start();
    }
    //endregion
}