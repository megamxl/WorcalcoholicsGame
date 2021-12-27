package Woralcoholics.game;

import javax.sound.sampled.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Player extends GameObject {

    GameManager handler;
    Game game;
    Camera cam;

    private final BufferedImage player_img;

    private double invincibleTime = 1000;
    private double wait;

    private float diagonalMultiplier = 1;
    private Boolean movingVertical = false;
    private Boolean movingHorizontal = false;
    private Clip sound;
    // better method would be to wait until the thread is finished and then start the new sound


    public Player(int x, int y, ID id, GameManager GameManager, Game game, Camera cam, Animations an) {
        super(x, y, id, an);
        this.handler = GameManager;
        this.game = game;
        this.cam = cam;

        player_img = an.getImage(1, 3, 64, 64);

    }

    @Override
    public void update() {

        x += velX;
        y += velY;

        checkIfGone();

        collision();
        if (handler.isL()) {
            try {
                handler.soundv = 2;
            } catch (Exception ex) {

            }
        }
        if (handler.isK()) {
            try {
                handler.soundv = 1;
            } catch (Exception ex) {
            }
        }
        if (handler.isM()) {
            try {
                handler.soundv = 0;
            } catch (Exception ex) {
            }
        }

        if (movingVertical && movingHorizontal) {
            diagonalMultiplier = 0.75f;
        } else {
            diagonalMultiplier = 1;
        }

        // Vertical Movement
        if (handler.isUp() && !handler.isDown()) {
            velY = -5 * diagonalMultiplier;
            movingVertical = true;
            playerMovementSound();
        }
        if (handler.isDown() && !handler.isUp()) {
            velY = 5 * diagonalMultiplier;
            movingVertical = true;
            playerMovementSound();
        }
        if ((handler.isUp() && handler.isDown()) || !handler.isUp() && !handler.isDown()) {
            velY = 0;
            movingVertical = false;
        }
        // Horizontal Movement
        if (handler.isRight() && !handler.isLeft()) {
            velX = 5 * diagonalMultiplier;
            movingHorizontal = true;
            playerMovementSound();
        }
        if (handler.isLeft() && !handler.isRight()) {
            velX = -5 * diagonalMultiplier;
            movingHorizontal = true;
            playerMovementSound();
        }
        if ((handler.isLeft() && handler.isRight()) || (!handler.isLeft() && !handler.isRight())) {
            velX = 0;
            movingHorizontal = false;
        }
    }

    @Override
    public void render(Graphics g) {
      /*  Graphics2D g2d = (Graphics2D) g;
        g.setColor(Color.GREEN);
        g2d.draw(getBounds());*/
        g.drawImage(player_img, (int) x, (int) y, null);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x + 12, (int) y, 40, 62);
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


    private void EnemyCharged(Object tempobject) {
        invincibleTime++;
        System.out.println(invincibleTime);
        if (invincibleTime % 30 == 0) {
            game.hp -= 10;
        }
    }

    /**
     * Collision Detection function for the Player
     * IMPORTANT:
     * The collision system is still WIP,
     * since colliding with a wall, disables even the movement alongside it as of right now.
     * Thats why the code is still a bit messy!!!
     */
    private void collision() {
        for (int i = 0; i < handler.object.size(); i++) {
            GameObject tempobject = handler.object.get(i);


            if (getBounds().intersects((tempobject.getBounds()))) {  //If player collides with another object...
                ID tempID = tempobject.getId();     //...get ID of said object...
                switch (tempID) {         //...and determine what should happen
                    case Block -> {
                        // change movement
                        // check for block coordinate to see where it is located in relation to the player
                        // or make it dependent on key pressed?
                        /*
                        if (x > tempobject.getX()) {
                            // is left from player
                            if ((x - 50) <= tempobject.getX()) {
                                x += velX * -1;
                            }
                        } else if (x < tempobject.getX()) {
                            // is right from player
                            if ((x + 50) >= tempobject.getX()) {
                                x += velX * -1;
                            }
                        }
                         */
                        x += velX * -1;
                        y += velY * -1;
                    }
                    case Crate -> {
                        game.ammo += 20;
                        handler.removeObject(tempobject);
                    }
                    case Enemy, EnemyBullet -> {
                        playerSoundHurt();
                        double now = System.currentTimeMillis();
                        if (game.hp > 0) {    //if player has health and is not invincible
                            switch (tempID) {
                                case EnemyBullet -> {
                                    handler.removeObject(tempobject);   //Remove Enemy Bullet if Player is hit
                                    game.hp -= 20;
                                }
                                case Enemy -> {
                                    if (handler.enemy.contains(tempobject)) { //enemy is on player
                                        invincibleTime++;
                                        //System.out.println(invincibleTime);
                                        if (invincibleTime % 30 == 0) { // %30 for how fast the HP is reducing
                                            game.hp -= 10;
                                        }
                                    } else {  //enemy is attacking player from a distance
                                        game.hp -= 10;
                                        handler.enemy.add(tempobject);
                                        //System.out.println(handler.enemy.size() + String.valueOf(tempobject));
                                    }

                                }
                            }
                            cam.shake = true;
                            //wait = now + invincibleTime;
                            // sound
                        }
                        if (game.hp <= 0) {
                            Game.setState(GameState.GAME_OVER);         //if the player has no HP left, its GAME OVER
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
    }

    /***
     * Runs the sound if player gets hurt
     */
    private void playerSoundHurt() {
        try {
            new Thread(() -> {

                try {
                    handler.playSoundHurt();
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
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
