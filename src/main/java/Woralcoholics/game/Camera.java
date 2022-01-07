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
     * Updates the camara positions and makes the screen shake on hit
     * @param object Game
     */
    public void update(GameObject object) {

        x += ((object.getX() - x) - game.SCREEN_WIDTH / 2 + 40) * 0.25f; // *0.1 makes the camera smoother
        y += ((object.getY() - y) - game.SCREEN_HEIGHT / 2 + 64) * 0.25f;
        //checking the corner cases if x or y are bigger than screen width or height rest them
        if (x <= 0) x = 0;
        if (x >= 1070) x = 1070;
        if (y <= 0) y = 0;
        if (y >= 500) y = 500;
        // Camera shake
        if(shake) {
            screenShake();
        }
    }

    private void screenShake(){
        if(shakeCycles == 0) {                      // check if shake should be executed
            shake = false;
            shakeCycles = 30;
        }
        // generate random wiggle variables
        x += game.randomNumber(-wiggle, wiggle);
        y += game.randomNumber(-wiggle, wiggle);
        shakeCycles--;                             // reduces shake cycles that screen does not shake infinity
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