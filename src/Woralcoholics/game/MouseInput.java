package Woralcoholics.game;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/***
 * in this class the mouse input  happens
 */
public class MouseInput extends MouseAdapter {

    private GameManager handler;
    private GameObject player;
    private Camera camera;
    private Game game;
    private Animations an;

    private double wait;
    private final double del = 200;

    public MouseInput(GameManager handler, Camera camera, Game game, Animations an) {
        this.handler = handler;
        this.camera = camera;
        this.game = game;
        this.an = an;
    }

    public void mousePressed(MouseEvent e) {
        for (int i = 0; i < handler.object.size(); i++) {
            if (handler.object.get(i).getId() == ID.Player) {
                player = handler.object.get(i);
                break;
            }
        }
        Point currentPos = e.getPoint();    //Grab current cursor position
        int button = e.getButton(); //Grab pressed button
        switch(game.currentState) {
            case TITLE -> {
                if(button == 1) game.currentState = GameState.MAIN_MENU;
            }
            case MAIN_MENU -> {
                if(button == 1) game.currentState = GameState.LEVEL;
                if(button == 3) game.currentState = GameState.OPTIONS;
            }
            case OPTIONS -> {
                if(button == 3) game.currentState = GameState.MAIN_MENU;
            }
            case PAUSE_MENU -> {
                if(button == 3) game.currentState = GameState.LEVEL;
            }
            case LEVEL -> {
                //currentPos.translate(-500, -282);

                /* 1 = LEFT MOUSE BUTTON
                 * 2 = MOUSE WHEEL
                 * 3 = RIGHT MOUSE BUTTON */
                switch (button) {
                    case 1 -> {
                        //System.out.println("BUTTON 1");
                        double now = System.currentTimeMillis();
                        //IF waiting time is over AND player has ammo -> shoot a bullet
                        if(now > wait && game.ammo >= 1) {
                            //Add camera pos, as bullets don't aim correctly otherwise
                            double mx = currentPos.x + camera.getX();
                            double my = currentPos.y + camera.getY();
                            //Middle of player coordinates
                            double px = player.getX()+32;
                            double py = player.getY()+32;
                            //Create a new bullet in the middle of player sprite (minus the bullet radius)
                            Bullet temp = new Bullet((int)px-4, (int)py-4, ID.Bullet, handler, an);
                            temp.direction(mx, my, px, py); //Calculate the direction of this bullet
                            handler.addObject(temp);    //Add the Bullet to the ObjectList
                            game.ammo--;    //Subtract 1 from ammo (bullet was shot)
                            //aSystem.out.println(game.ammo);
                            wait = now + del;   //Waiting time for next viable Input
                        }
                    }
                    case 2 -> System.out.println("BUTTON 2");
                    case 3 -> {
                        //System.out.println("BUTTON 3");
                        game.currentState = GameState.PAUSE_MENU;
                        //Grab current cursor position
                        //float mx = currentPos.x + camera.getX();
                        //float my = currentPos.y + camera.getY();
                        //ID m = getIDAt(mx, my); //Get ID of object at cursor
                        //System.out.println(m);
                    }
                    default -> {
                    }
                }
                //System.out.println(currentPos.x + " " + currentPos.y);
                //System.out.println(player.getX()+16 + " " + player.getY()+24);
            }
            case GAME_OVER -> {
                if(button == 1) {
                    game.currentState = GameState.LEVEL;
                }
            }
        }


    }

    public void mouseEntered(MouseEvent e) {
        //System.out.println("MOUSE ENTERED");
    }

    public void mouseExited(MouseEvent e) {
        //System.out.println("MOUSE EXITED");
    }

    public ID getIDAt(float x, float y) {   //Method for identifying objects
        for(int i = 0; i < handler.object.size(); i++) {
            GameObject temp = handler.object.get(i);
            if(temp.getBounds().contains(x, y)) {
                return temp.getId();
            }
        }
        return null;
    }
}
