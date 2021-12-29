package Woralcoholics.game;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.awt.event.MouseWheelEvent;

/***
 * in this class the mouse input  happens
 */
public class MouseInput extends MouseAdapter {

    private GameManager handler;
    private GameObject player;
    private Camera camera;
    private Game game;
    private Animations an;
    volatile private boolean mouseDown = false; //determine if mouse1 is pressed or not


    public MouseInput(GameManager handler, Camera camera, Game game, Animations an) {
        this.handler = handler;
        this.camera = camera;
        this.game = game;
        this.an = an;
    }


    //region MouseEvents
    public void mousePressed(MouseEvent e) {
        mouseDown = true;
        //System.out.println("mouse pressed");
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
            case MAIN_MENU, TUTORIAL, HIGH_SCORES, OPTIONS, PAUSE_MENU -> {
                if (button == 1) {
                    for (int i = 0; i < handler.object.size(); i++) {
                        GameObject temp = handler.object.get(i);
                        if (temp.getId() == ID.UIButton && temp.getBounds().contains(currentPos)) {
                            Game.setState(temp.nextState);
                        }
                    }
                }
            }
            case UPGRADE_MENU -> {
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
                        if (handler.del == 0) {
                            try {
                                new Thread(() -> {
                                    machinegun(e);
                                }).start();
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        } else {
                            //System.out.println("BUTTON 1");
                            handler.now = System.currentTimeMillis();
                            //IF waiting time is over AND player has ammo -> shoot a bullet
                            if (handler.now > handler.wait && game.ammo >= 1) {
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
                                //System.out.println(game.ammo);
                                handler.wait = handler.now + handler.del;   //Waiting time for next viable Input
                                playSoundGun(game.ammo);
                            } else if (handler.now > handler.wait && game.ammo <= 0) {
                                playSoundGun(game.ammo); //has no ammo
                                handler.wait = handler.now + handler.del;
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

    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            mouseDown = false;
            //System.out.println("mouse rel");
        }
    }

    public void mouseEntered(MouseEvent e) {
        //System.out.println("MOUSE ENTERED");
    }

    public void mouseExited(MouseEvent e) {
        //System.out.println("MOUSE EXITED");
        if (Game.getState() == GameState.LEVEL) {
            Game.setState(GameState.PAUSE_MENU);
        }
    }

    //endregion
    //region MouseWheelEvents
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            //System.out.println("Rotated Up... " + e.getWheelRotation()); weapon index +1
        } else {
            // System.out.println("Rotated Down... " + e.getWheelRotation()); weapon index -1
        }
    }
    //endregion
    //region PRIVATE METHODS

    /***
     * Runs the sound if player gets hurt
     * @param ammo
     */
    private void playSoundGun(int ammo) {
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
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
            }).start();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void machinegun(MouseEvent e) {
        //System.out.println("MACHINE");
        handler.now = System.currentTimeMillis();
        //IF waiting time is over AND player has ammo -> shoot a bullet
        while (mouseDown && game.ammo >= 1) {

            PointerInfo a = MouseInfo.getPointerInfo();
            Point point = new Point(a.getLocation());
            SwingUtilities.convertPointFromScreen(point, e.getComponent());
            int x = (int) point.getX();
            int y = (int) point.getY();
            //Add camera pos, as bullets don't aim correctly otherwise
            double mx = x + camera.getX();
            double my = y + camera.getY();
            //Middle of player coordinates
            double px = player.getX() + 32;
            double py = player.getY() + 32;
            //Create a new bullet in the middle of player sprite (minus the bullet radius)
            Bullet temp = new Bullet((int) px - 4, (int) py - 4, ID.Bullet, handler, an);
            temp.direction(mx, my, px, py); //Calculate the direction of this bullet
            handler.addObject(temp);    //Add the Bullet to the ObjectList
            game.ammo--;    //Subtract 1 from ammo (bullet was shot)
            playSoundGun(game.ammo);
            handler.wait = handler.now + handler.del;   //Waiting time for next viable Input
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        if (game.ammo <= 0) {
            playSoundGun(game.ammo); //has no ammo
        }
    }
//endregion
}
