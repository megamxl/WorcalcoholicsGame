package Woralcoholics.game;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// in this class the mouse input will happen

public class MouseInput extends MouseAdapter {

    private GameManager handler;
    private Camera camera;
    private Game game;

    public MouseInput(GameManager handler, Camera camera, Game game) {
        this.handler = handler;
        this.camera = camera;
        this.game = game;
    }

    public void mousePressed(MouseEvent e) {
        Point currentPos = e.getPoint();    //grab current Pos
        //currentPos.translate(-500, -282);
        int button = e.getButton(); //grab pressed button
        /* 1 = LEFT MOUSE BUTTON
         * 2 = MOUSE WHEEL
         * 3 = RIGHT MOUSE BUTTON */
        switch (button) {
            case 1 -> {
                //System.out.println("BUTTON 1");
                Bullet temp = new Bullet(500, 282, ID.Bullet);
                temp.handler = this.handler;
                temp.direction(currentPos.x, currentPos.y, 500, 282);
                handler.addObject(temp);
            }
            case 2 -> System.out.println("BUTTON 2");
            case 3 -> System.out.println("BUTTON 3");
            default -> {
            }
        }
        //System.out.println(currentPos.x + " " + currentPos.y);
    }

    public void mouseEntered(MouseEvent e) {
        System.out.println("MOUSE ENTERED");
    }

    public void mouseExited(MouseEvent e) {
        System.out.println("MOUSE EXITED");
    }

    /*
    private static final int BUTTON_COUNT = 3;
    //Positions of Cursor
    private Point mousePos = null;
    private Point currentPos = null;
    //Current State of Mouse Buttons
    private boolean[] state = null;
    //Polled mouse buttons
    private MouseState[] poll = null;

    private enum MouseState {
        RELEASED,
        PRESSED,
        ONCE
    }

    public MouseInput() {
        //Default mouse Pos
        mousePos = new Point(0,0);
        currentPos = new Point(0,0);
        //Initial button states
        state = new boolean[BUTTON_COUNT];
        poll = new MouseState[BUTTON_COUNT];
        for(int i = 0; i < BUTTON_COUNT; i++) {
            poll[i] = MouseState.RELEASED;
        }
    }

    public synchronized void poll() {
        //Save current Pos
        mousePos = new Point(currentPos);
        //Check mouse buttons
        for(int i = 0; i < BUTTON_COUNT; i++) {
            if(state[i]) {
                //button down after release (click)
                if(poll[i] == MouseState.RELEASED) {
                    poll[i] = MouseState.ONCE;
                }
                else    //button is kept down
                    poll[i] = MouseState.PRESSED;
            }
            else    //button is not down
                poll[i] = MouseState.RELEASED;
        }
    }

    public Point getMousePos() {
        return mousePos;
    }

    public boolean buttonDownOnce(int button) {
        return poll[button-1] == MouseState.ONCE;
    }

    public boolean buttonDown(int button) {
        return poll[button-1] == MouseState.ONCE ||
                poll[button-1] == MouseState.PRESSED;
    }

    public synchronized void mousePressed(MouseEvent e) {
        state[e.getButton()-1] = true;
    }

    public synchronized void mouseReleased(MouseEvent e) {
        state[e.getButton()-1] = false;
    }

    public synchronized void mouseEntered(MouseEvent e) {
        mouseMoved(e);
    }

    public synchronized void mouseExited(MouseEvent e) {
        mouseMoved(e);
    }

    public synchronized void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    public synchronized void mouseMoved(MouseEvent e) {
        currentPos = e.getPoint();
    }

    public synchronized void mouseClicked(MouseEvent e) {
        //
    }*/
}
