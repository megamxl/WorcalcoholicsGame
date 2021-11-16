package Woralcoholics.game;

import java.awt.*;

// we make this class abstract so it can only be extended bunt not initialized
public abstract class GameObject {

    // location of object
    protected /*int*/float x,y;
    // speed of object
    protected float velX =0, velY = 0;
    protected ID id;
    protected GameManager handler;

    public GameObject(int x, int y, ID id) {
        this.x = x;
        this.y = y;
        this.id = id;
    }

    // this function is the update function, because it gets called every second x amount of times
    // wee need this for making the calculation in our game
    public abstract void update();
    // the render function is there to tell the computer where to put the calculated values and how they should look
    public abstract void render(Graphics g);
    // this method is going to be used for all objects that move to check for collision
    public abstract Rectangle getBounds();


    // getter and setters
    public /*int*/ float getX() {return x;}

    public void setX(int x) {
        this.x = x;
    }

    public /*int*/float getY() {return y;}

    public void setY(int y) {
        this.y = y;
    }

    public float getVelX() {
        return velX;
    }

    public void setVelX(float velX) {
        this.velX = velX;
    }

    public float getVelY() {
        return velY;
    }

    public void setVelY(float velY) {
        this.velY = velY;
    }

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }
}



