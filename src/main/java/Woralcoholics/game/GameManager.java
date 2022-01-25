package Woralcoholics.game;

import javax.sound.sampled.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

/**
 * first Idea
 * https://www.youtube.com/watch?v=nXzTp61FKR4
 *
 * @author Maxlimilian Nowak
 * @author Christoph Oprawill
 */

public class GameManager {

    // making a linked list for storing all our GameObjects, so we can iterate threw ist every frame when we want to compare
    // or render
    LinkedList<GameObject> object = new LinkedList<GameObject>();
    LinkedList<Object> enemy = new LinkedList<Object>();
    LinkedList<Bullet> bullets = new LinkedList<Bullet>();

    //In this script we define the 4 ways somebody can walk and set and get them
    private boolean up = false, down = false, right = false, left = false;
    private boolean ll = false, kk = false, mm = false;
    Clip sound, soundenemy, soundplayer;
    public Clip backgroundsound;
    private final int timeOfBackgroundSound = 198000;  // 3:18 Min length of backgroundsound
    Path relativePath;
    Path absolutePath;
    FloatControl volume;
    public static int soundv = 1; //default value for -40f sound | MUTE (Without Sound Effects just background music) -> -80f | MAX -> 6.0206f
    protected boolean IsSoundPlayingMove, IsSoundPlayingPlayerHurt, isSoundPlayingEquip, isBackgroundSoundPlaying, swordIsSwung = false;
    protected double wait;
    //machine gun - del=0 | normal gun - del=200 | slowgun - del=1000
    protected int del = 1000; //how fast player can shoot, less -> faster
    protected double now;
    protected double nowForKeys;
    private double previousms = 0;
    protected boolean ammo = true;
    protected boolean reloaded = true;
    protected int weaponIndex = 0;
    protected Weapon selectedWeapon;
    public float angle; // from bullet
    public boolean playerIsInit = false; // prevents that the MouseEvents gets NullPointerException

    /**
     * updates the all tempobjects in the Linked list
     */
    public void update() {
        // loop over the complete list of GameObjects and execute the update Function from the abstract class
        for (int i = 0; i < object.size(); i++) {
            try {
                GameObject tempObject = object.get(i);
                tempObject.update();
            } catch (Exception ex) {
                System.out.println("crash");
                ex.printStackTrace();
            }
        }
        for (int i = 0; i < bullets.size(); i++) {
            Bullet temp = bullets.get(i);
            if (temp.inGame) {
                temp.update();
            }
        }
    }

    /**
     * renders the all tempobjects in the Linked list
     */
    public void render(Graphics g) {
        // loop over the complete list of GameObjects and execute the render Function from the abstract class
        for (int i = 0; i < object.size(); i++) {
            GameObject tempObject = object.get(i);
            tempObject.render(g);
        }
        for (int i = 0; i < bullets.size(); i++) {
            Bullet temp = bullets.get(i);
            if (temp.inGame) {
                temp.render(g);
            }
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
        //tempObject = null;
    }

    /***
     * A function to clear all objects in the handler
     */
    public void clearHandler() {
        int i = 0;
        while (this.object.size() > 0) {
            this.object.remove(0);
        }
    }

    public void hideBullets() {
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).setId(ID.Bullet);
            bullets.get(i).inGame = false;
            bullets.get(i).setPos(0, 0);
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
                temp = null;

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
    public void playSoundEnemyDead() throws LineUnavailableException, UnsupportedAudioFileException, IOException, InterruptedException {
        sound = AudioSystem.getClip();
        Path relativePath = Paths.get("Resource/Sound/enemyhurt2.wav");
        Path absolutePath = relativePath.toAbsolutePath();
        sound.open(AudioSystem.getAudioInputStream(new File(absolutePath.toString())));
        FloatControl volume = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
        if (soundv == 0) {
            volume.setValue(-80f); // MUTE
            //System.out.println("VOLUME MUTE + " +volume.toString());
        } else if (soundv == 1) {
            volume.setValue(-30f); // DEFAULT
            //System.out.println("VOLUME DEFAULT + " +volume.toString());
        } else if (soundv == 2) {
            volume.setValue(-8f); // Maximum
            //System.out.println("VOLUME UP + " +volume.toString());
        }
        sound.start();
        Thread.sleep(100000);
    }

    public void playSoundEnemyHit() throws LineUnavailableException, UnsupportedAudioFileException, IOException, InterruptedException {
        sound = AudioSystem.getClip();
        Path relativePath = Paths.get("Resource/Sound/enemyhurt3.wav");
        Path absolutePath = relativePath.toAbsolutePath();
        sound.open(AudioSystem.getAudioInputStream(new File(absolutePath.toString())));
        FloatControl volume = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
        if (soundv == 0) {
            volume.setValue(-80f); // MUTE
            //System.out.println("VOLUME MUTE + " +volume.toString());
        } else if (soundv == 1) {
            volume.setValue(-30f); // DEFAULT
            //System.out.println("VOLUME DEFAULT + " +volume.toString());
        } else if (soundv == 2) {
            volume.setValue(-8f); // Maximum
            //System.out.println("VOLUME UP + " +volume.toString());
        }
        sound.start();
        Thread.sleep(100000);
    }

    public void playSoundDestroyedBox(boolean fullycracked) throws LineUnavailableException, UnsupportedAudioFileException, IOException, InterruptedException {
        sound = AudioSystem.getClip();
        Path relativePath = Paths.get("Resource/Sound/boxhitted.wav");
        Path absolutePath = relativePath.toAbsolutePath();
        sound.open(AudioSystem.getAudioInputStream(new File(absolutePath.toString())));
        FloatControl volume = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
        if (soundv == 0) {
            volume.setValue(-80f); // MUTE
            //System.out.println("VOLUME MUTE + " +volume.toString());
        } else if (soundv == 1) {
            volume.setValue(-22f); // DEFAULT
            //System.out.println("VOLUME DEFAULT + " +volume.toString());
        } else if (soundv == 2) {
            volume.setValue(0f); // Maximum
            //System.out.println("VOLUME UP + " +volume.toString());
        }
        sound.start();
        if (fullycracked)
            Thread.sleep(100000);
        else
            Thread.sleep(100);  //-> if the box gets damage and is not fully destroyed use this Value
        sound.stop();
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
            volume.setValue(-80f);
        } else if (soundv == 1) {
            volume.setValue(-40f);
        } else if (soundv == 2) {
            volume.setValue(-18f);
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
        // just get played once
        if (!isBackgroundSoundPlaying) {
            isBackgroundSoundPlaying = true;
            backgroundsound = AudioSystem.getClip();
            Path relativePath = Paths.get("Resource/Sound/backgroundsound.wav");
            Path absolutePath = relativePath.toAbsolutePath();
            backgroundsound.open(AudioSystem.getAudioInputStream(new File(absolutePath.toString())));
            FloatControl volume = (FloatControl) backgroundsound.getControl(FloatControl.Type.MASTER_GAIN);
            if (soundv == 0) {
                volume.setValue(-80f);
            } else if (soundv == 1) {
                volume.setValue(-22f);
            } else if (soundv == 2) {
                volume.setValue(0f);
            }
            backgroundsound.start();
            Thread.sleep(timeOfBackgroundSound);
            backgroundsound.stop();
            isBackgroundSoundPlaying = false; // looping the backgroundsound in game class
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
    public void playSoundPlayerHurt() throws LineUnavailableException, UnsupportedAudioFileException, IOException, InterruptedException {
        if (IsSoundPlayingPlayerHurt == false) {
            IsSoundPlayingPlayerHurt = true;
            Clip sound = AudioSystem.getClip();
            Path relativePath = Paths.get("Resource/Sound/playerhurt.wav");
            Path absolutePath = relativePath.toAbsolutePath();
            sound.open(AudioSystem.getAudioInputStream(new File(absolutePath.toString())));
            FloatControl volume = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
            if (soundv == 0) {
                volume.setValue(-80f);
            } else if (soundv == 1) {
                volume.setValue(-40f);
            } else if (soundv == 2) {
                volume.setValue(-18f);
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
                volume.setValue(-80f);
            } else if (soundv == 1) {
                volume.setValue(-37f);
            } else if (soundv == 2) {
                volume.setValue(-15f);
            }
            sound.start();
            Thread.sleep(100);
            sound.stop();
            IsSoundPlayingMove = false;
        }
    }

    public void playSoundEquip(boolean error) throws LineUnavailableException, UnsupportedAudioFileException, IOException, InterruptedException {
        WeaponType selected = selectedWeapon.getType();
        if (isSoundPlayingEquip == false) {
            isSoundPlayingEquip = true;
            Clip sound = AudioSystem.getClip();
            Path relativePath;
            if (error == true) {
                relativePath = Paths.get("Resource/Sound/gunequiperror.wav");
            } else {
                if (selected == WeaponType.Sword) {
                    relativePath = Paths.get("Resource/Sound/swordequip.wav");
                } else {
                    relativePath = Paths.get("Resource/Sound/gunequip.wav");
                }
            }
            Path absolutePath = relativePath.toAbsolutePath();
            sound.open(AudioSystem.getAudioInputStream(new File(absolutePath.toString())));
            FloatControl volume = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
            if (soundv == 0) {
                volume.setValue(-80f);
            } else if (soundv == 1 && !error) {
                if (selected == WeaponType.Sword) {
                    volume.setValue(-30f); // sword equip on K
                } else {
                    volume.setValue(-40f); // gun equip on K
                }
            } else if (soundv == 1 && error) {
                volume.setValue(-23f); // errorsound on K
            } else if (soundv == 2 && !error) {
                if (selected == WeaponType.Sword) {
                    volume.setValue(-8f); // sword equip on L
                } else {
                    volume.setValue(-18f); //gun equip on L
                }
            } else if (soundv == 2 && error) {
                volume.setValue(-1f); // errorsound on L
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
            volume.setValue(-80f);
        } else if (soundv == 1) {
            volume.setValue(-40f);
        } else if (soundv == 2) {
            volume.setValue(-18f);
        }
        sound.start();
    }

    /**
     * plays the sound for the weapon of the player
     *
     * @param ammo
     * @throws LineUnavailableException
     * @throws UnsupportedAudioFileException
     * @throws IOException
     * @throws InterruptedException
     * @throws IllegalArgumentException
     */
    public void playSoundWeapon(int ammo) throws LineUnavailableException, UnsupportedAudioFileException, IOException, InterruptedException, IllegalArgumentException {
        WeaponType selected = selectedWeapon.getType();
        switch (selected) {
            case Pistol -> {
                if (ammo <= 0) {
                    relativePath = Paths.get("Resource/Sound/pistolempty.wav");
                } else {
                    relativePath = Paths.get("Resource/Sound/pistolfire3.wav");
                }
            }
            case Shotgun -> {
                if (ammo <= 0) {
                    relativePath = Paths.get("Resource/Sound/shotgunempty.wav");
                } else {
                    relativePath = Paths.get("Resource/Sound/shotgunfire.wav");
                }
            }
            case MachineGun -> {
                if (ammo <= 0) {
                    relativePath = Paths.get("Resource/Sound/machinegunempty.wav");
                } else {
                    relativePath = Paths.get("Resource/Sound/machinegunfire.wav");
                }
            }
            case Sword -> {
                relativePath = Paths.get("Resource/Sound/sword4.wav"); //sword3 auch gut
            }
        }
        volume = getClip();
        switch (soundv) {
            case 0 -> volume.setValue(-80f);
            case 1 -> {
                if (ammo <= 0) {
                    switch (selected) {
                        case Pistol -> volume.setValue(-30f);
                        case Shotgun -> volume.setValue(-25f);
                        case MachineGun -> volume.setValue(-22f);
                        case Sword -> volume.setValue(-40f);
                    }
                } else {
                    switch (selected) {
                        case Pistol -> volume.setValue(-45f);
                        case Shotgun, MachineGun, Sword -> volume.setValue(-40f);
                    }
                }
            }
            case 2 -> {
                if (ammo <= 0) {
                    switch (selected) {
                        case Pistol -> volume.setValue(-8f);
                        case Shotgun -> volume.setValue(-3f);
                        case MachineGun -> volume.setValue(0f);
                    }
                } else {
                    switch (selected) {
                        case Pistol -> volume.setValue(-23f);
                        case Shotgun, MachineGun -> volume.setValue(-18f);
                    }
                }
            }
        }
        sound.start();
        Thread.sleep(1000);

    }

    private FloatControl getClip() throws LineUnavailableException, UnsupportedAudioFileException, IOException, IllegalStateException {
        sound = AudioSystem.getClip();
        absolutePath = relativePath.toAbsolutePath();
        sound.open(AudioSystem.getAudioInputStream(new File(absolutePath.toString())));
        FloatControl volume = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
        return volume;
    }


    /***
     * the keys for the sound
     */
    public void keySounds() {
        nowForKeys = System.currentTimeMillis(); // set to 2 seconds to change the sound volumes prevents program crashing
        if (isL() && soundv != 2 && nowForKeys - previousms >= 2000) {
            try {
                soundv = 2;
                backgroundsound.close();
                isBackgroundSoundPlaying = false; // set it to false that a new sound can play
                previousms = nowForKeys;
                playBackgroundSound();
            } catch (Exception ex) {
            }
        } else if (isK() && soundv != 1 && nowForKeys - previousms >= 2000) {
            try {
                soundv = 1;
                backgroundsound.close();
                isBackgroundSoundPlaying = false;
                previousms = nowForKeys;
                playBackgroundSound();
            } catch (Exception ex) {
            }
        } else if (isM() && soundv != 0 && nowForKeys - previousms >= 2000) {
            try {
                soundv = 0;
                backgroundsound.close();
                isBackgroundSoundPlaying = false;
                previousms = nowForKeys;
                playBackgroundSound();
            } catch (Exception ex) {
            }
        }
    }

    public void playSoundAmmoReload() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        WeaponType selected = selectedWeapon.getType();
        switch (selected) {
            case Pistol -> relativePath = Paths.get("Resource/Sound/pistolreload.wav");
            case Shotgun -> relativePath = Paths.get("Resource/Sound/shotgunreload.wav");
            case MachineGun -> relativePath = Paths.get("Resource/Sound/machinegunreload.wav");
            case Sword -> relativePath = Paths.get("Resource/Sound/swordreload.wav");
        }
        volume = getClip();
        switch (soundv) {
            case 0 -> {
                volume.setValue(-80f);
            }
            case 1 -> {
                switch (selected) {
                    case Pistol, Shotgun -> volume.setValue(-30f);
                    case MachineGun -> volume.setValue(-28f);
                    case Sword -> volume.setValue(-15f);
                }
            }
            case 2 -> {
                switch (selected) {
                    case Pistol, Shotgun -> volume.setValue(-8);
                    case MachineGun -> volume.setValue(-6f);
                    case Sword -> volume.setValue(7f);
                }
            }
        }
        sound.start();
    }
}