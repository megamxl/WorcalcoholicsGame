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
 * @author Gustavo Podzuweit
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
    int[] coordinatesadditive = new int[2]; // x and y Coordinates of Gun Sprite
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
        coordinatesadditive[0] = 42;
        coordinatesadditive[1] = 25;
        handler.playerIsInit = true;
    }

    @Override
    public void update() {
        // makes the player move by adding their velocity to their respective axis/direction
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
        validateCoordinates();

        if (Game.inTutorial) {
            tutorial();
        }
        //System.out.println(x + " " + y);
    }

    private void validateCoordinates() {
        //350° - 5°
        if (handler.angle > 0 && handler.angle <= 90) {
            int y = (int) (25 + (handler.angle *0.1)); // adding manually angle to y value
            coordinatesadditive[0] = 42;
            coordinatesadditive[1] = y; // slow to direction +

            System.out.println("0-90°");
            // start of 90° more aggressive manipulation to x direction implement
        } else if (handler.angle > 90 && handler.angle <= 120) {
            int x = (int) (87 - (handler.angle) / 2); //  manipulating x coordinates into minus so that start value from angle before is similar
            coordinatesadditive[0] = x; //42 -> start value at angle 90°
            coordinatesadditive[1] = 34;  // 25 + (90*0,1) = 34 -> start value
            System.out.println("90-120°");

        }
        else if (handler.angle > 120 && handler.angle <= 180) {
            int x = (int) (47 - (handler.angle) / 6); //  manipulating x coordinates into minus
            coordinatesadditive[0] = x; //27 -> start value at angle 120°
            coordinatesadditive[1] = 34;
            System.out.println("120-180°");

        }
        else if (handler.angle > 180 && handler.angle <= 230) {
            int y = (int) (106 - (handler.angle) / 2.5); // manipulating y coordinates minus direction
            coordinatesadditive[0] = 17;
            coordinatesadditive[1] = y; //34 -> start value at angle 180°
            System.out.println("180-230°");
        }
        else if (handler.angle > 230 && handler.angle <= 270) {
            int x = (int) (-98 + (handler.angle) / 2);  //manipulating x coordinates + direction
            int y = (int) (25.5 - (handler.angle*0.05)); //manipulating y coordinates - direction
            coordinatesadditive[0] = x; //17 -> start value at angle 230°
            coordinatesadditive[1] = y; // 14 -> start value at angle 230°
            System.out.println("230-270°");
        }
        else if (handler.angle > 270 && handler.angle <= 330) {
            int x = (int) (-98 + (handler.angle) / 2); // manipulate x to + direction
            int y = (int) (-10.5 + (handler.angle/12)); // manipulate y to + direction
            coordinatesadditive[0] = 37; //37 -> start value at angle 270°
            coordinatesadditive[1] = y; // 12 -> start value at angle 270°
            System.out.println("270-330°");
        }
        else if (handler.angle > 330 && handler.angle <= 360) { // x and y correct
            int x = (int) (-98 + (handler.angle) / 2);
            coordinatesadditive[0] = 37; //37 -> start value at angle 330°
            coordinatesadditive[1] = 17; // 17 -> start value at angle 330°
            System.out.println("330-360°");
        }
        else {
            //System.out.println(handler.angle);
            System.out.println("not implemented");
        }
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
        g.drawImage(player_gun_img, ((int) x) + coordinatesadditive[0], ((int) y) + coordinatesadditive[1], null); // x and y adjustable for gun position

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

    // collider for horizontal player movement
    public Rectangle getBoundsX() {
        Rectangle tempX = getBounds();
        tempX.width += 9.4;
        tempX.x -= 4.5f;
        return tempX;
    }

    // collider for vertical player movement
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
        int width;
        int height;
        if (handler.selectedgun.getType() == GunType.Pistol) {
            width = 19;
            height = 19;
            player_gun_img = an.getImage(colrow[0], colrow[1], width, height); // pistol
        }
        if (handler.selectedgun.getType() == GunType.Shotgun) {
            width = 32;
            height = 19;
            player_gun_img = an.getImage(colrow[0], colrow[1], width, height); //shotgun
        } else {
            width = 32;
            height = 19;
            player_gun_img = an.getImage(colrow[0], colrow[1], width, height); //machine gun
        }

        final double rads = Math.toRadians(handler.angle);
        final double sin = Math.abs(Math.sin(rads));
        final double cos = Math.abs(Math.cos(rads));
        final int w = (int) Math.floor(player_gun_img.getWidth() * cos + player_gun_img.getHeight() * sin);
        final int h = (int) Math.floor(player_gun_img.getHeight() * cos + player_gun_img.getWidth() * sin);
        final BufferedImage rotatedImage = new BufferedImage(w, h, player_gun_img.getType());
        final AffineTransform at = new AffineTransform();
        at.translate(w / 2, h / 2);
        at.rotate(rads, 0, 0);
        at.translate(-player_gun_img.getWidth() / 2, -player_gun_img.getHeight() / 2);
        final AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        rotateOp.filter(player_gun_img, rotatedImage);
        player_gun_img = rotatedImage;
    }

    /**
     * Collision Detection function for the Player
     */
    private void collision() {
        for (int i = 0; i < handler.object.size(); i++) {
            GameObject tempobject = handler.object.get(i);

            // --------------------------------------------------------------
            // checks if the player collides with the wall horizontally
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
            // checks if the player collides with the wall vertically
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
                        // stops movement if the player collides with a wall with their inner collider
                        // the inner collider should however be covered by the horizontal and vertical collider
                        // meaning it only functions as a safety measure if both of these should somehow fail
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

    // checks if the movement keys are currently pressed and increases the respective directional velocity
    // also, if the player moves diagonally, the players' movement speed gets reduced by a percentage
    // since moving diagonally means, vertical and horizontal velocity are getting added together, which would
    // make the player too fast
    private void movement() {
        handler.playerIsInit = true;
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