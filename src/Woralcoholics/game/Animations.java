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
        return img.getSubimage((col*32)-32, (row*32)-32,width,height);
    }

}
