package Woralcoholics.game;

import javax.sound.sampled.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

public class GameManager {

    // making a linked list for storing all our GameObjects, so we can iterate threw ist every frame when we want to compare
    // or render
    LinkedList<GameObject> object = new LinkedList<GameObject>();
    LinkedList<Object> enemy = new LinkedList<Object>();

    //In this script we define the 4 ways somebody can walk and set and get them
    private boolean up = false, down = false, right = false, left = false;
    private boolean ll = false, kk = false, mm = false;
    Clip sound;
    public Clip backgroundsound;
    public int soundv = 1; //default value for -20f sound
    protected boolean IsSoundPlayingMove, IsSoundPlayingPlayerHurt, isSoundPlayingEquip = false;
    protected double wait;
    //machine gun - del=0 | normal gun - del=200 | slowgun - del=1000
    protected int del = 1000; //how fast player can shoot, less -> faster
    protected double now;
    protected boolean ammo = true;
    protected boolean reloaded = true;
    protected int gunindex = 1;
    protected Gun selectedgun;


    /**
     * updates the all tempobjects in the Linked list
     */
    public void update() {
        for (int i = 0; i < object.size(); i++) {
            try {
                GameObject tempObject = object.get(i);

                tempObject.update();
            } catch (Exception ex) {
                System.out.println("crash");
                ex.printStackTrace();
            }
        }

    }

    /**
     * renders the all tempobjects in the Linked list
     */
    public void render(Graphics g) {
        for (int i = 0; i < object.size(); i++) {
            GameObject tempObject = object.get(i);

            tempObject.render(g);
        }
    }

    public void render(Graphics g, ID id) {
        for (int i = 0; i < object.size(); i++) {
            GameObject tempObject = object.get(i);
            if (tempObject.getId() == id) tempObject.render(g);
        }
    }

    // these function enable us to add and remove objects from our Handler to

    public void addObject(GameObject tempObject) {
        object.add(tempObject);
    }

    public void removeObject(GameObject tempObject) {
        object.remove(tempObject);
    }

    /***
     * A function to clear all objects in the handler
     */
    public void clearHandler() {
        while (this.object.size() > 0) {
            this.object.remove(0);
        }
    }

    /***
     * A function to clear all instances of one specific object in the handler
     */
    public void clearObjects(ID toClear) {
        for (int i = this.object.size() - 1; i > 0; i--) {
            GameObject temp = this.object.get(i);
            if (temp.getId() == toClear) {
                this.object.remove(temp);
            }
        }
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isL() {
        return ll;
    }

    public void setL(boolean l) {
        this.ll = l;
    }

    public boolean isK() {
        return kk;
    }

    public void setK(boolean k) {
        this.kk = k;
    }

    public boolean isM() {
        return mm;
    }

    public void setM(boolean m) {
        this.mm = m;
    }


    /**
     * plays the sound for the enemy
     *
     * @throws LineUnavailableException
     * @throws UnsupportedAudioFileException
     * @throws IOException
     * @throws InterruptedException
     */
    public void playSoundEnemy() throws LineUnavailableException, UnsupportedAudioFileException, IOException, InterruptedException {
        sound = AudioSystem.getClip();
        Path relativePath = Paths.get("Resource/enemyhurt2.wav");
        Path absolutePath = relativePath.toAbsolutePath();
        sound.open(AudioSystem.getAudioInputStream(new File(absolutePath.toString())));
        FloatControl volume = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
        if (soundv == 0) {
            volume.setValue(-80f); // MUTE
            //System.out.println("VOLUME MUTE + " +volume.toString());
        } else if (soundv == 1) {
            volume.setValue(-10f); // DEFAULT -> more than -20 because the sound is per default very quietly
            //System.out.println("VOLUME DEFAULT + " +volume.toString());
        } else if (soundv == 2) {
            volume.setValue(6.0206f); // Maximum
            //System.out.println("VOLUME UP + " +volume.toString());
        }
        sound.start();
        Thread.sleep(100000);
    }

    /**
     * plays the sound for the gun of the player
     *
     * @param ammo
     * @throws LineUnavailableException
     * @throws UnsupportedAudioFileException
     * @throws IOException
     * @throws InterruptedException
     * @throws IllegalArgumentException
     */
    public void playSoundGun(int ammo) throws LineUnavailableException, UnsupportedAudioFileException, IOException, InterruptedException, IllegalArgumentException {
        sound = AudioSystem.getClip();
        Path relativePath;
        if (ammo <= 0) {
            relativePath = Paths.get("Resource/gunzeroammo.wav");
        } else {
            relativePath = Paths.get("Resource/gunplayer2.wav");
        }
        Path absolutePath = relativePath.toAbsolutePath();
        sound.open(AudioSystem.getAudioInputStream(new File(absolutePath.toString())));
        FloatControl volume = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
        if (soundv == 0) {
            volume.setValue(-80f); // MUTE
            //System.out.println("VOLUME MUTE + " +volume.toString());
        } else if (soundv == 1) {
            if (ammo <= 0) {
                volume.setValue(-5f); //Default adjusted
            } else {
                volume.setValue(-20f); // Default
            }
            //System.out.println("VOLUME DEFAULT + " +volume.toString());
        } else if (soundv == 2) {
            volume.setValue(6.0206f); // Maximum
            //System.out.println("VOLUME UP + " +volume.toString());
        }
        sound.start();
        Thread.sleep(10000);
    }

    /**
     * plays the sound for the gunnerenemy
     *
     * @throws LineUnavailableException
     * @throws UnsupportedAudioFileException
     * @throws IOException
     * @throws InterruptedException
     */
    public void playSoundGunnerEnemy() throws LineUnavailableException, UnsupportedAudioFileException, IOException, InterruptedException {
        sound = AudioSystem.getClip();
        Path relativePath = Paths.get("Resource/gunnerenemyfire2.wav");
        Path absolutePath = relativePath.toAbsolutePath();
        sound.open(AudioSystem.getAudioInputStream(new File(absolutePath.toString())));
        FloatControl volume = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
        if (soundv == 0) {
            volume.setValue(-80f); // MUTE
            //System.out.println("VOLUME MUTE + " +volume.toString());
        } else if (soundv == 1) {
            volume.setValue(-15f); // DEFAULT -> more than -20 because the sound is per default very quietly
            //System.out.println("VOLUME DEFAULT + " +volume.toString());
        } else if (soundv == 2) {
            volume.setValue(6.0206f); // Maximum
            //System.out.println("VOLUME UP + " +volume.toString());
        }
        sound.start();
        Thread.sleep(100000);
    }

    /**
     * plays the background sound
     *
     * @throws LineUnavailableException
     * @throws UnsupportedAudioFileException
     * @throws IOException
     * @throws InterruptedException
     */
    public void playBackgroundSound() throws LineUnavailableException, UnsupportedAudioFileException, IOException, InterruptedException {
        backgroundsound = AudioSystem.getClip();
        Path relativePath = Paths.get("Resource/backgroundsound.wav");
        Path absolutePath = relativePath.toAbsolutePath();
        backgroundsound.open(AudioSystem.getAudioInputStream(new File(absolutePath.toString())));
        FloatControl volume = (FloatControl) backgroundsound.getControl(FloatControl.Type.MASTER_GAIN);
        if (soundv == 0) {
            volume.setValue(-80f); // MUTE
        } else if (soundv == 1) {
            volume.setValue(-35f); // DEFAULT -> less than -20 because the sound is per default very loud
        } else if (soundv == 2) {
            volume.setValue(6.0206f); // Maximum
        }
        backgroundsound.start();
    }

    /**
     * plays the sound for the player
     *
     * @throws LineUnavailableException
     * @throws UnsupportedAudioFileException
     * @throws IOException
     * @throws InterruptedException
     */
    public void playSoundPlayerHurt() throws LineUnavailableException, UnsupportedAudioFileException, IOException, InterruptedException {
        if (IsSoundPlayingPlayerHurt == false) {
            IsSoundPlayingPlayerHurt = true;
            Clip sound = AudioSystem.getClip();
            Path relativePath = Paths.get("Resource/playerhurt.wav");
            Path absolutePath = relativePath.toAbsolutePath();
            sound.open(AudioSystem.getAudioInputStream(new File(absolutePath.toString())));
            FloatControl volume = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
            if (soundv == 0) {
                volume.setValue(-80f); // NormalSound
                //System.out.println("VOLUME MUTE + " +volume.toString());
            } else if (soundv == 1) {
                volume.setValue(-20f); // DEFAULT
                //System.out.println("VOLUME DEFAULT + " +volume.toString());
            } else if (soundv == 2) {
                volume.setValue(6.0206f); // Maximum
                //System.out.println("VOLUME UP + " +volume.toString());
            }
            sound.start();
            Thread.sleep(1000);
            sound.stop();
            IsSoundPlayingPlayerHurt = false;
        } else {
            //waiting till the sound is finished, otherwise there would be more than 1 sound playing at once
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
    public void playerMovementSound() throws LineUnavailableException, UnsupportedAudioFileException, IOException, InterruptedException {
        if (IsSoundPlayingMove == false) {
            IsSoundPlayingMove = true;
            Clip sound = AudioSystem.getClip();
            Path relativePath = Paths.get("Resource/move2.wav");
            Path absolutePath = relativePath.toAbsolutePath();
            sound.open(AudioSystem.getAudioInputStream(new File(absolutePath.toString())));
            FloatControl volume = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
            if (soundv == 0) {
                volume.setValue(-80f); // MUTE
                //System.out.println("VOLUME MUTE + " +volume.toString());
            } else if (soundv == 1) {
                volume.setValue(-15f); // DEFAULT -> balanced default sound
                //System.out.println("VOLUME DEFAULT + " +volume.toString());
            } else if (soundv == 2) {
                volume.setValue(6.0206f); // Maximum
                //System.out.println("VOLUME UP + " +volume.toString());
            }
            sound.start();
            Thread.sleep(100);
            sound.stop();
            IsSoundPlayingMove = false;
        } else {
            //waiting till the sound is finished, otherwise there would be more than 1 sound playing at once
        }
    }

    public void playSoundEquip(boolean error) throws LineUnavailableException, UnsupportedAudioFileException, IOException, InterruptedException {
        if (isSoundPlayingEquip == false) {
            isSoundPlayingEquip = true;
            Clip sound = AudioSystem.getClip();
            Path relativePath;
            if (error == true) {
                relativePath = Paths.get("Resource/gunequiperror.wav");
            } else {
                relativePath = Paths.get("Resource/gunequip.wav");
            }
            Path absolutePath = relativePath.toAbsolutePath();
            sound.open(AudioSystem.getAudioInputStream(new File(absolutePath.toString())));
            FloatControl volume = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
            if (soundv == 0) {
                volume.setValue(-80f); // MUTE
                //System.out.println("VOLUME MUTE + " +volume.toString());
            } else if (soundv == 1 && error == false) {
                volume.setValue(-15f); // DEFAULT -> balanced default sound
                //System.out.println("VOLUME DEFAULT + " +volume.toString());
            } else if (soundv == 1 && error == true) {
                volume.setValue(-5f); // DEFAULT -> balanced default sound
                //System.out.println("VOLUME DEFAULT + " +volume.toString());
            } else if (soundv == 2) {
                volume.setValue(6.0206f); // Maximum
                //System.out.println("VOLUME UP + " +volume.toString());
            }
            sound.start();
            Thread.sleep(100);
            isSoundPlayingEquip = false;
            Thread.sleep(220);
            sound.stop();
        } else {
            //waiting till the sound is finished, otherwise there would be more than 1 sound playing at once
        }
    }

    public void playSoundGameOver() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        Clip sound = AudioSystem.getClip();
        Path relativePath = Paths.get("Resource/gameover2.wav");
        Path absolutePath = relativePath.toAbsolutePath();
        sound.open(AudioSystem.getAudioInputStream(new File(absolutePath.toString())));
        FloatControl volume = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
        if (soundv == 0) {
            volume.setValue(-80f); // MUTE
        } else if (soundv == 1) {
            volume.setValue(-15f); // DEFAULT -> less than -20 because the sound is per default very loud
        } else if (soundv == 2) {
            volume.setValue(6.0206f); // Maximum
        }
        sound.start();
    }
}
