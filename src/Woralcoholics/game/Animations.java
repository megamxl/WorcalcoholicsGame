package Woralcoholics.game;

import java.awt.image.BufferedImage;

public class Animations {

    private BufferedImage img;

    public Animations(BufferedImage img)
    {
        this.img =img;
    }


    //32px x 32px
    public BufferedImage getImage(int col,int row,int width, int height)
    {
        return img.getSubimage((col*256)-256, (row*256)-256,width,height);
    }

}
