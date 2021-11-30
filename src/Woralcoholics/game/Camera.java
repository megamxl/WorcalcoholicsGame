package Woralcoholics.game;

// the class that makes the Playercamera
public class Camera {

    float x, y;

    public Camera(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void update(GameObject object) {
        x += ((object.getX() - x) - 1000 / 2) * 0.05f; //*0.05f should make it more smooth
        y += ((object.getY() - y) - 563 / 2) * 0.05f;
        if (x <= 0) x = 0;
        if (x >= 1070) x = 1070;
        if (y <= 0) y = 0;
        if (y >= 500) y = 500;
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