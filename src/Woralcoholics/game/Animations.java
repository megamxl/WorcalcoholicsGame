package Woralcoholics.game;

import java.awt.image.BufferedImage;


/**
 * Animation Class for the sprites assigned to the objects
 */
public class Animations {
    private BufferedImage img;

    public Animations(BufferedImage img)
    {
        this.img =img;
    }


    /**
     * A function to get a sub image out of a Images
     * @param col col of the image *64
     * @param row row of the image *64
     * @param width the width of the sub picture
     * @param height the height of the sub picture
     * @return the sub image
     */
    public BufferedImage getImage(int col,int row,int width, int height)
    {
        return img.getSubimage((col*64)-64, (row*64)-64,width,height);
    }

}
