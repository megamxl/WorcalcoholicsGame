package Woralcoholics.game;

// the class that makes the Playercamera
public class Camera {

    float x, y;
    Game game;
    protected boolean shake = false;
    private int shakeCycles = 30;   //how long the camera should shake (ticks)
    private final int wiggle = 5;   //how many pixels to wiggle

    public Camera(float x, float y, Game game) {
        this.x = x;
        this.y = y;
        this.game = game;
    }

    /**
     * Updates the camara positions and makes the screnn shake on hit
     * @param object Game
     */
    public void update(GameObject object) {

        x += ((object.getX() - x) - 1024 / 2) * 0.05f; //*0.05f should make it more smooth
        y += ((object.getY() - y) - 576 / 2) * 0.05f;
        if (x <= 0) x = 0;
        if (x >= 1070) x = 1070;
        if (y <= 0) y = 0;
        if (y >= 500) y = 500;
        if(shake) {
            if(shakeCycles == 0) {
                shake = false;
                shakeCycles = 30;
            }
            x += game.randomNumber(-wiggle, wiggle);
            y += game.randomNumber(-wiggle, wiggle);
            shakeCycles--;
        }
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