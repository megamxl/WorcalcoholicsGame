package Woralcoholics.game;

import java.awt.*;

public class UIButton extends GameObject{

    private GameManager handler;
    private Game game;
    private boolean isPressed = false;

    private int width, height;
    private String name;
    private GameState nextState;

    public UIButton(int x, int y, int width, int height, String name, GameState nextState, ID id, /*GameManager handler,*/ Game game, Animations an) {
        super(x, y, id, an);
        this.width = width;
        this.height = height;
        this.name = name;
        this.nextState = nextState;
        //this.handler = handler;
        this.game = game;
    }

    @Override
    public void update() {

    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.RED);
        g.drawRect((int)x, (int)y, width, height);
        g.setColor(Color.WHITE);
        g.drawString(name, width/2, height/2);
    }


    @Override
    public Rectangle getBounds() {
        return null;
    }
}
