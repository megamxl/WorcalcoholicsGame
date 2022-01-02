package Woralcoholics.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Animations {

    private int speed;
    private int frames;

    private int index = 0;
    private int count = 0;
    private int counter =0;

    private BufferedImage[] images;
    private BufferedImage currentImages;

    public Animations(int speed, BufferedImage... args) {
        this.speed = speed;
        images = new BufferedImage[args.length];
        for (int i = 0; i < args.length; i++) {
            images[i] = args[i];
        }
        frames = args.length;
    }

    public void runAnimations() {
        index++;
        if (index > speed) {
            index = 0;
            nextFrame();
        }
    }

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

    public void renderAnimation(Graphics g, int x, int y) {

        g.drawImage(currentImages, x, y, null);

    }

    public void renderAnimation(Graphics g, int x, int y, int scaleX, int scaleY) {

        g.drawImage(currentImages, x, y, scaleX, scaleY, null);

    }
}



