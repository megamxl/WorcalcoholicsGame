package Woralcoholics.game;

import Woralcoholics.game.GameObject;
import Woralcoholics.game.ID;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Block extends GameObject {

    private final BufferedImage block_img;

    public Block(int x, int y, ID id, Animations an) {

        super(x, y, id, an);

        block_img = an.getImage(1,1,32,32);
    }

    @Override
    public void update() {

    }

    @Override
    public void render(Graphics g) {
        g.drawImage(block_img, (int)x, (int)y,null);
         /* g.setColor(Color.black);
        g.fillRect((int)x,(int)y,32,32);*/
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int)x,(int)y, 32,32);
    }
}
