package Woralcoholics.game;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// in this class the mouse input will happen

public class MouseInput extends MouseAdapter {

    private GameManager handler;
    private GameObject player;
    private Camera camera;
    private Game game;
    private Animations an;

    private double wait;
    private final double del = 500;

    public MouseInput(GameManager handler, Camera camera, Game game, GameObject player, Animations an) {
        this.handler = handler;
        this.camera = camera;
        this.game = game;
        this.player = player;
        this.an = an;
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
                double now = System.currentTimeMillis();
                if(now > wait) {
                    double mx = currentPos.x + camera.getX();   //add camera pos, as bullets don't aim correctly otherwise
                    double my = currentPos.y + camera.getY();
                    double px = player.getX()+16;
                    double py = player.getY()+24;
                    Bullet temp = new Bullet((int)px-4, (int)py-4, ID.Bullet, handler, game, an);
                    temp.handler = this.handler;
                    temp.direction(mx, my, px, py);
                    handler.addObject(temp);
                    wait = now + del;
                }
            }
            case 2 -> System.out.println("BUTTON 2");
            case 3 -> {
                //System.out.println("BUTTON 3");
                float mx = currentPos.x + camera.getX();
                float my = currentPos.y + camera.getY();
                ID m = getIDAt(mx, my);
                System.out.println(m);
            }
            default -> {
            }
        }
        //System.out.println(currentPos.x + " " + currentPos.y);
        //System.out.println(player.getX()+16 + " " + player.getY()+24);
    }

    public void mouseEntered(MouseEvent e) {
        System.out.println("MOUSE ENTERED");
    }

    public void mouseExited(MouseEvent e) {
        System.out.println("MOUSE EXITED");
    }

    public ID getIDAt(float x, float y) {
        for(int i = 0; i < handler.object.size(); i++) {
            GameObject temp = handler.object.get(i);
            if(temp.getBounds().contains(x, y)) {
                return temp.getId();
            }
        }
        return null;
    }
}
