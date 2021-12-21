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



    public BufferedImage getImage(int col,int row,int width, int height)
    {
        return img.getSubimage((col*64)-64, (row*64)-64,width,height);
    }

}
