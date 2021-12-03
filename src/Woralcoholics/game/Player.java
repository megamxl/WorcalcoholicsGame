package Woralcoholics.game;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Player extends GameObject {

    GameManager handler;
    Game game;

    private final BufferedImage player_img;

    private final double invincibleTime = 1000;
    private double wait;

    private float diagonalMultiplier = 1;
    private Boolean movingVertical = false;
    private Boolean movingHorizontal = false;
    // better method would be to wait until the thread is finished and then start the new sound
    private boolean IsSoundPlaying = false;
    private boolean IsSoundPlaying2 = false;

    public Player(int x, int y, ID id, GameManager GameManager, Game game, Animations an) {
        super(x, y, id, an);
        this.handler = GameManager;
        this.game = game;

        player_img = an.getImage(1,3,64,64);

    }

    @Override
    public void update() {

        x += velX;
        y += velY;

        collision();

        if(movingVertical && movingHorizontal) {
            diagonalMultiplier = 0.75f;
        }
        else {
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
    private void playSound() throws LineUnavailableException, UnsupportedAudioFileException, IOException, InterruptedException {
        if(IsSoundPlaying==false) {
            IsSoundPlaying=true;
            Clip sound = AudioSystem.getClip();
            Path relativePath = Paths.get("Resource/move.wav");
            Path absolutePath = relativePath.toAbsolutePath();
            sound.open(AudioSystem.getAudioInputStream(new File(absolutePath.toString())));
            sound.start();
            Thread.sleep(100);
            sound.stop();
            IsSoundPlaying=false;
        }
        else
        {
            //waiting till the sound is finished, otherwise there would be more than 1 sound playing at once

        }
    }
    private void playSoundHurt() throws LineUnavailableException, UnsupportedAudioFileException, IOException, InterruptedException {
        if(IsSoundPlaying2==false) {
            IsSoundPlaying2=true;
            Clip sound = AudioSystem.getClip();
            Path relativePath = Paths.get("Resource/playerhurt.wav");
            Path absolutePath = relativePath.toAbsolutePath();
            sound.open(AudioSystem.getAudioInputStream(new File(absolutePath.toString())));
            sound.start();
            Thread.sleep(1000);
            sound.stop();
            IsSoundPlaying2=false;
        }
        else
        {
            //waiting till the sound is finished, otherwise there would be more than 1 sound playing at once

        }
    }

    @Override
    public void render(Graphics g) {
      /*  Graphics2D g2d = (Graphics2D) g;
        g.setColor(Color.GREEN);
        g2d.draw(getBounds());*/
        g.drawImage(player_img,(int)x, (int)y, null);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int)x+12,(int)y, 40, 62);
    }

    private void collision() {
        for (int i = 0; i < handler.object.size(); i++) {

            GameObject tempobject = handler.object.get(i);

            if (tempobject.getId() == ID.Block) {
                if (getBounds().intersects(tempobject.getBounds())) {
                    // change movement
                    // check for block coordinate to see where it is located in relation to the player
                    // or make it dependent on key pressed?

                    /*
                    System.out.println("WALL X: " + tempobject.getX());
                    System.out.println("PLAY X:" + x);
                     */
                    if(x > tempobject.getX())
                    {
                        // is left from player
                        // System.out.println("LEFT");
                        if((x - 50) <= tempobject.getX())
                        {
                            x += velX * -1;
                        }
                    }
                    else if (x < tempobject.getX())
                    {
                        // is right from player
                        // System.out.println("RIGHT");
                        if((x + 50) >= tempobject.getX())
                        {
                            x += velX * -1;
                        }
                    }

                    /*
                    System.out.println("WALL Y: " + tempobject.getY());
                    System.out.println("PLAY Y:" + y);
                     */
                    y += velY * -1;
                    // System.out.println("--------------");
                }
            }
            if(tempobject.getId() == ID.Create) {
                if (getBounds().intersects(tempobject.getBounds())) {
                    game.ammo += 20;
                    handler.removeObject(tempobject);
                }
            }

            if(tempobject.getId() == ID.Enemy || tempobject.getId() == ID.EnemyBullet) {    //If Player is hit by Enemy of EnemyBullet
                ID temp = tempobject.getId();
                if (getBounds().intersects(tempobject.getBounds())) {
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
                        e.printStackTrace();}
                    double now = System.currentTimeMillis();
                    if (game.hp > 1 && now > wait) {
                        switch (temp) {
                            case EnemyBullet -> {
                                handler.removeObject(tempobject);   //Remove Enemy Bullet if Player is hit
                                game.hp -= 20;
                            }
                            case Enemy -> game.hp -= 10;
                        }
                        wait = now + invincibleTime;
                        // sound
                    }
                    if (game.hp == 1) {
                        game.hp = game.hp - 1;
                        System.out.println("Game Over!");
                    }
                    if(game.hp == 0){
                        game.hp = 0;
                    }

                }

            }
        }
    }
}
