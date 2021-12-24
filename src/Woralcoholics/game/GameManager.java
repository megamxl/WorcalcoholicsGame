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
    public int soundv = 1; //default value for -20f sound


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

    // these function enable us to add and remove objects from our Handler to

    public void addObject(GameObject tempObject) {
        object.add(tempObject);
    }

    public void removeObject(GameObject tempObject) {
        object.remove(tempObject);
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

    public void playSoundGun(boolean ammo) throws LineUnavailableException, UnsupportedAudioFileException, IOException, InterruptedException, IllegalArgumentException {
        sound = AudioSystem.getClip();
        Path relativePath;
        if (!ammo) {
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
            if (!ammo) {
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
}
