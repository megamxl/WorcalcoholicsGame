package Woralcoholics.game;

// the class that makes the Playercamera
public class Camera {

    float x,y;

    public Camera(float x, float y){
        this.x =x;
        this.y =y;
    }

    public void update(GameObject object){
        this.x = ((object.getX() - this.x) - 1000 / 2) * 0.05f; //*0.05f should make it more smooth
        this.y = ((object.getY() - this.y) - 1000 / 2) * 0.05f;
        if(this.x <= 0) this.x = 0;
        if(this.x >= 1000) this.x = 1000;
        if(this.y <= 0) this.y = 0;
        if(this.y >= 563) this.y = 563;
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