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
    Path relativePath;
    Path absolutePath;
    FloatControl volume;
    public int soundv = 1; //default value for -40f sound | MUTE -> -80f | MAX -> 6.0206f
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
        Path relativePath = Paths.get("Resource/Sound/enemyhurt2.wav");
        Path absolutePath = relativePath.toAbsolutePath();
        sound.open(AudioSystem.getAudioInputStream(new File(absolutePath.toString())));
        FloatControl volume = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
        if (soundv == 0) {
            volume.setValue(-80f); // MUTE
            //System.out.println("VOLUME MUTE + " +volume.toString());
        } else if (soundv == 1) {
            volume.setValue(-30f); // DEFAULT -> more than -20 because the sound is per default very quietly
            //System.out.println("VOLUME DEFAULT + " +volume.toString());
        } else if (soundv == 2) {
            volume.setValue(6.0206f); // Maximum
            //System.out.println("VOLUME UP + " +volume.toString());
        }
        sound.start();
        Thread.sleep(100000);
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
        Path relativePath = Paths.get("Resource/Sound/gunnerenemyfire2.wav");
        Path absolutePath = relativePath.toAbsolutePath();
        sound.open(AudioSystem.getAudioInputStream(new File(absolutePath.toString())));
        FloatControl volume = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
        if (soundv == 0) {
            volume.setValue(-80f); // MUTE
            //System.out.println("VOLUME MUTE + " +volume.toString());
        } else if (soundv == 1) {
            volume.setValue(-40f); // DEFAULT -> more than -20 because the sound is per default very quietly
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
        Path relativePath = Paths.get("Resource/Sound/backgroundsound.wav");
        Path absolutePath = relativePath.toAbsolutePath();
        backgroundsound.open(AudioSystem.getAudioInputStream(new File(absolutePath.toString())));
        FloatControl volume = (FloatControl) backgroundsound.getControl(FloatControl.Type.MASTER_GAIN);
        if (soundv == 0) {
            volume.setValue(-80f);
        } else if (soundv == 1) {
            volume.setValue(-55f);
        } else if (soundv == 2) {
            volume.setValue(6.0206f);
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
            Path relativePath = Paths.get("Resource/Sound/playerhurt.wav");
            Path absolutePath = relativePath.toAbsolutePath();
            sound.open(AudioSystem.getAudioInputStream(new File(absolutePath.toString())));
            FloatControl volume = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
            if (soundv == 0) {
                volume.setValue(-80f); // NormalSound
            } else if (soundv == 1) {
                volume.setValue(-40f); // DEFAULT
            } else if (soundv == 2) {
                volume.setValue(6.0206f); // Maximum
            }
            sound.start();
            Thread.sleep(1000);
            sound.stop();
            IsSoundPlayingPlayerHurt = false;
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
            Path relativePath = Paths.get("Resource/Sound/move2.wav");
            Path absolutePath = relativePath.toAbsolutePath();
            sound.open(AudioSystem.getAudioInputStream(new File(absolutePath.toString())));
            FloatControl volume = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
            if (soundv == 0) {
                volume.setValue(-80f); // MUTE
            } else if (soundv == 1) {
                volume.setValue(-37f); // DEFAULT -> balanced default sound
            } else if (soundv == 2) {
                volume.setValue(6.0206f); // Maximum
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
                relativePath = Paths.get("Resource/Sound/gunequiperror.wav");
            } else {
                relativePath = Paths.get("Resource/Sound/gunequip.wav");
            }
            Path absolutePath = relativePath.toAbsolutePath();
            sound.open(AudioSystem.getAudioInputStream(new File(absolutePath.toString())));
            FloatControl volume = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
            if (soundv == 0) {
                volume.setValue(-80f); // MUTE
            } else if (soundv == 1 && !error) {
                volume.setValue(-40f);
            } else if (soundv == 1 && error) {
                volume.setValue(-23f);
            } else if (soundv == 2) {
                volume.setValue(6.0206f); // Maximum
            }
            sound.start();
            Thread.sleep(100);
            isSoundPlayingEquip = false;
            Thread.sleep(220);
            sound.stop();
        }
    }

    public void playSoundGameOver() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        Clip sound = AudioSystem.getClip();
        Path relativePath = Paths.get("Resource/Sound/gameover2.wav");
        Path absolutePath = relativePath.toAbsolutePath();
        sound.open(AudioSystem.getAudioInputStream(new File(absolutePath.toString())));
        FloatControl volume = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
        if (soundv == 0) {
            volume.setValue(-80f); // MUTE
        } else if (soundv == 1) {
            volume.setValue(-40f); // DEFAULT -
        } else if (soundv == 2) {
            volume.setValue(6.0206f); // Maximum
        }
        sound.start();
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
        if (GunType.Pistol.equals(selectedgun.getType())) {
            if (ammo <= 0) {
                relativePath = Paths.get("Resource/Sound/pistolempty.wav");
            } else {
                relativePath = Paths.get("Resource/Sound/pistolfire3.wav");
            }
        } else if (GunType.Shotgun.equals(selectedgun.getType())) {
            if (ammo <= 0) {
                relativePath = Paths.get("Resource/Sound/shotgunempty.wav");
            } else {
                relativePath = Paths.get("Resource/Sound/shotgunfire.wav");
            }
        } else if (GunType.MachineGun.equals(selectedgun.getType())) {
            if (ammo <= 0) {
                relativePath = Paths.get("Resource/Sound/machinegunempty.wav");
            } else {
                relativePath = Paths.get("Resource/Sound/machinegunfire.wav");
            }
        }

        volume = getClip();
        if (soundv == 0) {
            volume.setValue(-80f);
        } else if (soundv == 1) {
            if (ammo <= 0) {
                if (GunType.Pistol.equals(selectedgun.getType())) {
                    volume.setValue(-30f);
                } else if (GunType.Shotgun.equals(selectedgun.getType())) {
                    volume.setValue(-25f);
                } else if (GunType.MachineGun.equals(selectedgun.getType())) {
                    volume.setValue(-22f);
                }
            } else {
                if (GunType.Shotgun.equals(selectedgun.getType()) || GunType.MachineGun.equals(selectedgun.getType())) {
                    volume.setValue(-40f);
                } else {
                    volume.setValue(-45f);
                }
            }
        } else if (soundv == 2) {
            volume.setValue(6.0206f);
        }
        sound.start();
        Thread.sleep(1000);

    }

    private FloatControl getClip() throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        sound = AudioSystem.getClip();
        absolutePath = relativePath.toAbsolutePath();
        sound.open(AudioSystem.getAudioInputStream(new File(absolutePath.toString())));
        FloatControl volume = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
        return volume;
    }

    public void playSoundAmmoReload() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        if (GunType.Pistol.equals(selectedgun.getType())) {
            relativePath = Paths.get("Resource/Sound/pistolreload.wav");
        } else if (GunType.Shotgun.equals(selectedgun.getType())) {
            relativePath = Paths.get("Resource/Sound/shotgunreload.wav");
        } else if (GunType.MachineGun.equals(selectedgun.getType())) {
            relativePath = Paths.get("Resource/Sound/machinegunreload.wav");
        }
        volume = getClip();
        if (soundv == 0) {
            volume.setValue(-80f);
        } else if (soundv == 1) {
            if (GunType.Pistol.equals(selectedgun.getType()) || GunType.Shotgun.equals(selectedgun.getType())) {
                volume.setValue(-30f);
            } else {
                volume.setValue(-28f);
            }
        } else if (soundv == 2) {
            volume.setValue(6.0206f);
        }
        sound.start();

    }
}
