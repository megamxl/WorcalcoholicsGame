package Woralcoholics.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Crate extends GameObject{

    private BufferedImage crate_image = null;

    public Crate(int x, int y, ID id, Animations an){
        super(x, y, id, an);
        //crate_image = an.getImage()
    }

    public void update() {

    }


    public void render(Graphics g) {
        g.setColor(Color.cyan);
        g.fillRect((int)x, (int)y, 32, 32);
    }

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, 32, 32);
    }
}
