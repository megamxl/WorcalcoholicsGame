package Woralcoholics.game;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

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
    private boolean ammo = true;

    public MouseInput(GameManager handler, Camera camera, Game game, Animations an) {
        this.handler = handler;
        this.camera = camera;
        this.game = game;
        this.an = an;
    }

    public void mousePressed(MouseEvent e) {
        //Get the player object for its coordinates
        for (int i = 0; i < handler.object.size(); i++) {
            if (handler.object.get(i).getId() == ID.Player) {
                player = handler.object.get(i);
                break;
            }
        }
        Point currentPos = e.getPoint();    //Grab current cursor position
        int button = e.getButton(); //Grab pressed button
        switch (Game.getState()) {  //depending on currentState, execute the following...
            case TITLE -> {
                if (button == 1) Game.setState(GameState.MAIN_MENU);
            }
            case MAIN_MENU -> {
                if (button == 1) Game.setState(GameState.LEVEL);
                if (button == 3) Game.setState(GameState.OPTIONS);
            }
            case OPTIONS -> {
                if (button == 3) Game.setState(GameState.MAIN_MENU);
            }
            case PAUSE_MENU -> {
                if (button == 3) Game.setState(GameState.LEVEL);
            }
            case UPGRADE_MENU ->{
                if (button == 1) Game.setState(GameState.LEVEL);
                Game.TimerValue = 5;    //5 secs wait time
                Game.shouldTime = true; //activate Timer
                Game.timerAction = 1;   //execute timerAction 1 -> countdown till next wave
            }
            case LEVEL -> {
                /* 1 = LEFT MOUSE BUTTON
                 * 2 = MOUSE WHEEL
                 * 3 = RIGHT MOUSE BUTTON */
                switch (button) {
                    case 1 -> {
                        //System.out.println("BUTTON 1");
                        double now = System.currentTimeMillis();
                        //IF waiting time is over AND player has ammo -> shoot a bullet
                        if (now > wait && game.ammo >= 1) {
                            //Add camera pos, as bullets don't aim correctly otherwise
                            double mx = currentPos.x + camera.getX();
                            double my = currentPos.y + camera.getY();
                            //Middle of player coordinates
                            double px = player.getX() + 32;
                            double py = player.getY() + 32;
                            //Create a new bullet in the middle of player sprite (minus the bullet radius)
                            Bullet temp = new Bullet((int) px - 4, (int) py - 4, ID.Bullet, handler, an);
                            temp.direction(mx, my, px, py); //Calculate the direction of this bullet
                            handler.addObject(temp);    //Add the Bullet to the ObjectList
                            game.ammo--;    //Subtract 1 from ammo (bullet was shot)
                            //aSystem.out.println(game.ammo);
                            wait = now + del;   //Waiting time for next viable Input
                            if (game.ammo <= 0) {
                                ammo = false;
                            }
                            try {
                                new Thread(() -> {

                                    try {
                                        handler.playSoundGun(ammo);
                                    } catch (LineUnavailableException ex) {
                                        ex.printStackTrace();
                                    } catch (UnsupportedAudioFileException ex) {
                                        ex.printStackTrace();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    } catch (InterruptedException ex) {
                                        ex.printStackTrace();
                                    }
                                }).start();
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                    }
                    case 2 -> System.out.println("BUTTON 2");
                    case 3 -> Game.setState(GameState.PAUSE_MENU);
                    default -> {
                    }
                }
            }
            case GAME_OVER -> {
                if (button == 1) {
                    Game.setState(GameState.LEVEL);
                }
            }
        }


    }

    public void mouseEntered(MouseEvent e) {
        //System.out.println("MOUSE ENTERED");
    }

    public void mouseExited(MouseEvent e) {
        //System.out.println("MOUSE EXITED");
        if(Game.getState() == GameState.LEVEL) {
            Game.setState(GameState.PAUSE_MENU);
        }
    }
}
