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
    private boolean IsSoundPlaying = false;
    private boolean IsSoundPlaying2 = false;


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
            try {
                new Thread(() -> {
                    try {
                        playSound();
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
        if (handler.isDown() && !handler.isUp()) {
            velY = 5 * diagonalMultiplier;
            movingVertical = true;
            try {
                new Thread(() -> {
                    try {
                        playSound();
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
        if ((handler.isUp() && handler.isDown()) || !handler.isUp() && !handler.isDown()) {
            velY = 0;
            movingVertical = false;
        }
        // Horizontal Movement
        if (handler.isRight() && !handler.isLeft()) {
            velX = 5 * diagonalMultiplier;
            movingHorizontal = true;
            try {
                new Thread(() -> {
                    try {
                        playSound();
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
        if (handler.isLeft() && !handler.isRight()) {
            velX = -5 * diagonalMultiplier;
            movingHorizontal = true;
            try {
                new Thread(() -> {

                    try {
                        playSound();
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
        if ((handler.isLeft() && handler.isRight()) || (!handler.isLeft() && !handler.isRight())) {
            velX = 0;
            movingHorizontal = false;
        }
    }

    /**
     * plays the sound for moving player
     *
     * @throws LineUnavailableException
     * @throws UnsupportedAudioFileException
     * @throws IOException
     * @throws InterruptedException
     */
    private void playSound() throws LineUnavailableException, UnsupportedAudioFileException, IOException, InterruptedException {
        if (IsSoundPlaying == false) {
            IsSoundPlaying = true;
            Clip sound = AudioSystem.getClip();
            Path relativePath = Paths.get("Resource/move5.wav");
            Path absolutePath = relativePath.toAbsolutePath();
            sound.open(AudioSystem.getAudioInputStream(new File(absolutePath.toString())));
            FloatControl volume = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
            if (handler.soundv == 0) {
                volume.setValue(-80f); // MUTE
                //System.out.println("VOLUME MUTE + " +volume.toString());
            } else if (handler.soundv == 1) {
                volume.setValue(-15f); // DEFAULT -> balanced default sound
                //System.out.println("VOLUME DEFAULT + " +volume.toString());
            } else if (handler.soundv == 2) {
                volume.setValue(6.0206f); // Maximum
                //System.out.println("VOLUME UP + " +volume.toString());
            }
            sound.start();
            Thread.sleep(100);
            sound.stop();
            IsSoundPlaying = false;
        } else {
            //waiting till the sound is finished, otherwise there would be more than 1 sound playing at once
        }
    }

    /**
     * plays the sound for the player
     *
     * @throws LineUnavailableException
     * @throws UnsupportedAudioFileException
     * @throws IOException
     * @throws InterruptedException
     */
    private void playSoundHurt() throws LineUnavailableException, UnsupportedAudioFileException, IOException, InterruptedException {
        if (IsSoundPlaying2 == false) {
            IsSoundPlaying2 = true;
            Clip sound = AudioSystem.getClip();
            Path relativePath = Paths.get("Resource/playerhurt.wav");
            Path absolutePath = relativePath.toAbsolutePath();
            sound.open(AudioSystem.getAudioInputStream(new File(absolutePath.toString())));
            FloatControl volume = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
            if (handler.soundv == 0) {
                volume.setValue(-80f); // NormalSound
                //System.out.println("VOLUME MUTE + " +volume.toString());
            } else if (handler.soundv == 1) {
                volume.setValue(-20f); // DEFAULT
                //System.out.println("VOLUME DEFAULT + " +volume.toString());
            } else if (handler.soundv == 2) {
                volume.setValue(6.0206f); // Maximum
                //System.out.println("VOLUME UP + " +volume.toString());
            }
            sound.start();
            Thread.sleep(1000);
            sound.stop();
            IsSoundPlaying2 = false;
        } else {
            //waiting till the sound is finished, otherwise there would be more than 1 sound playing at once
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
        if ((y > 1054 || y < 64) || (x > 1900 || x < 0)) {
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
                        if (x > tempobject.getX()) {
                            // is left from player
                            // System.out.println("LEFT");
                            if ((x - 50) <= tempobject.getX()) {
                                x += velX * -1;
                            }
                        } else if (x < tempobject.getX()) {
                            // is right from player
                            // System.out.println("RIGHT");
                            if ((x + 50) >= tempobject.getX()) {
                                x += velX * -1;
                            }
                        }
                    /*
                    System.out.println("WALL Y: " + tempobject.getY());
                    System.out.println("PLAY Y:" + y);
                     */
                        y += velY * -1;
                    }
                    case Crate -> {
                        game.ammo += 20;
                        handler.removeObject(tempobject);
                    }
                    case Enemy, EnemyBullet -> {
                        try {
                            new Thread(() -> {

                                try {
                                    playSoundHurt();
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
                        if (game.hp == 0) {
                            game.currentState = GameState.GAME_OVER;         //if the player has no HP left, its GAME OVER
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
}
