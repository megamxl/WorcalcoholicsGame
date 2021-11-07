package Woralcoholics.game;

import java.awt.*;

public class Box extends GameObject{
    public Box(int x, int y, ID id) {
        super(x, y, id);
    }

    @Override
    public void update() {
        x += velX;
        y += velY;

    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.magenta);
        g.fillRect(x,y,32,32);
    }

    @Override
    public Rectangle getBounds() {
        return null;
    }
}
