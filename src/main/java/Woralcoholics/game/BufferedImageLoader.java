package Woralcoholics.game;
// this class is for loading the sprites and getting in the game

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class BufferedImageLoader {
    // gets the image as bufferd Image type
    private BufferedImage image;

    /***
     * the function that returns the wanted image or throws exception
     */
    public BufferedImage loadImage(String path) throws IOException {
        image = ImageIO.read(getClass().getResource(path)); // using the imageIO class from java
        return image;
    }
}