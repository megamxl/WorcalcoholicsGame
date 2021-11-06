package Woralcoholics.game;

// the class that makes the Playercamera
public class Camera {

    float x,y;

    public Camera(float x, float y){
        this.x =x;
        this.y =y;
    }

    public void update(GameObject object){

    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }




}
