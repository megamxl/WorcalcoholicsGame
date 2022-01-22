package Woralcoholics.game;

import java.awt.*;
import java.util.Random;

public class SwordHitbox extends GameObject {
    public double x2, y2;

    private double swingSpeed = 0.5;
    private double swingRadius = Math.PI/2;
    private double length = 100;
    private double startAlpha = 0, alpha = 0;
    private final int swingDirection;
    private boolean remove;

    private GameManager handler;

    public SwordHitbox(float x, float y, ID id, GameManager handler, ImageGetter an) {
        super(x, y, id, an);
        this.handler = handler;
        remove = false;
        int randomDirection = new Random().nextInt(2);
        if(randomDirection == 0) randomDirection = -1;
        swingDirection = randomDirection;
    }

    @Override
    public void update() {
        alpha += swingDirection*swingSpeed/(2*Math.PI);
        //checkAngle();
        getPlayerCoords();
        x2 = x+Math.cos(alpha) * length;
        y2 = y+Math.sin(alpha) * length;
        //System.out.println(x2 + " " + y2);
        if((alpha <= startAlpha - swingRadius && swingDirection < 0) ||
                (alpha >= startAlpha + swingRadius && swingDirection > 0)|| remove) {
            handler.removeObject(this);
        }
        collision();
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.RED);
        //g.fillRect((int)x, (int)y,(int)(x2),(int)(y2));
        g.drawLine((int)x, (int)y,(int)x2, (int)y2);
    }

    private void collision() {
        for(GameObject object : handler.object) {
            if(object.getId() == ID.Block) {
                if(object.getBounds().intersectsLine(x,y,x2,y2)) {
                    remove = true;
                }
            }
        }
    }



    public void startingDirection(double mx, double my) {
        startAlpha = Math.atan2(my-y, mx-x);
        startAlpha += -1*swingDirection*swingRadius/2;
        x2 = x+Math.cos(startAlpha) * length;
        y2 = y+Math.sin(startAlpha) * length;
        alpha = startAlpha;
    }

    private void checkAngle() {
        if(alpha > Math.PI) {
            alpha -= Math.PI;
        }
        if(alpha < Math.PI) {
            alpha += Math.PI;
        }
    }

    private void getPlayerCoords() {
        for(GameObject object : handler.object) {
            if(object.getId() == ID.Player) {
                x = object.getX() + 32;
                y = object.getY() + 32;
                break;
            }
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, (int) x2, (int) y2);
    }
}
