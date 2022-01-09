package Woralcoholics.game;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * inspiration
 * @author Maxlimilian Nowak
 */

public class Animations {

    private int speed;
    private int frames;

    private int index = 0;
    private int count = 0;
    private int counter =0;

    private BufferedImage[] images;
    private BufferedImage currentImages;
    private GameManager manager;

    private boolean finished;

    public Animations(int speed, BufferedImage... args) {
        this.speed = speed;
        this.manager = manager;
        images = new BufferedImage[args.length];
        for (int i = 0; i < args.length; i++) {
            images[i] = args[i];
        }
        frames = args.length;

        finished = false;

    }

    /**
     * runs the animation unlimited
     */
    public void runAnimations() {
        index++;
        if (index > speed) {
            index = 0;
            nextFrame();
        }
    }

    /**
     * runs the animation once with the given length
     * @param length
     */
    public void runAnimationsOnce(int length) {
        index++;
        if (index > speed) {
            index = 0;
            if(counter<=length) {
                nextFrame();
                counter++;
            }
        }

    }


    /**
     * selects the next frame in the Buffered Image array
     */
    public void nextFrame() {
        for (int i = 0; i < frames; i++) {
            if (count == i) {
                currentImages = images[i];
            }
        }
        count++;
        if (count > frames) {
            count = 0;
        }
    }

    /**
     * renders Animation with x and y coordinates
     * @param g Graphics object
     * @param x
     * @param y
     */
    public void renderAnimation(Graphics g, int x, int y) {g.drawImage(currentImages, x, y, null);}

    /**
     * renders Animation with x and y coordinates and scales the Image
     * @param g
     * @param x
     * @param y
     * @param scaleX
     * @param scaleY
     */
    public void renderAnimation(Graphics g, int x, int y, int scaleX, int scaleY) {g.drawImage(currentImages, x, y, scaleX, scaleY, null);}
}



