package Woralcoholics.game;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.Window;
import java.awt.event.*;
import java.io.IOException;
import java.awt.event.MouseWheelEvent;
import java.awt.Robot;

/**
 * in this class the mouse input  happens
 *
 * @author Christoph Oprawill
 */

public class MouseInput extends MouseAdapter {

    private GameManager handler;
    private GameObject player;
    private Camera camera;
    private Game game;
    private ImageGetter an;
    private Gun gun;
    volatile private boolean mouseDown = false; //determine if mouse1 is pressed or not
    private boolean gunequiperror = false;
    Upgrades upgrades;

    public MouseInput(GameManager handler, Camera camera, Game game, ImageGetter an, Gun gun) {
        this.handler = handler;
        this.camera = camera;
        this.game = game;
        this.an = an;
        this.upgrades = new Upgrades(game);
        this.gun = gun;
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
            case TITLE, MAIN_MENU, HIGH_SCORES, OPTIONS, PAUSE_MENU, UPGRADE_MENU, CREDITS, GAME_OVER -> {
                if (button == 1) {
                    for (int i = 0; i < handler.object.size(); i++) {
                        GameObject temp = handler.object.get(i);
                        if (temp.getId() == ID.UIButton && temp.getBounds().contains(currentPos)) {
                            temp.action();
                            break;
                        }
                    }
                    if (Game.getState() == GameState.UPGRADE_MENU) {
                        Game.TimerValue = 5;    //5 secs wait time
                        Game.shouldTime = true; //activate Timer
                        Game.timerAction = 1;   //execute timerAction 1 -> countdown till next wave
                    }
                }
            }
            case LEVEL, TUTORIAL -> {
                /** 1 = LEFT MOUSE BUTTON
                 * 2 = MOUSE WHEEL
                 * 3 = RIGHT MOUSE BUTTON **/
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
                        } else if (handler.del == 1000) {
                            try {
                                new Thread(() -> {
                                    shotgun(e);
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
                                temp.direction(mx, my, px, py, false, 0); //Calculate the direction of this bullet
                                handler.addObject(temp);    //Add the Bullet to the ObjectList
                                playSoundGun(game.ammo);
                                game.ammo--;    //Subtract 1 from ammo (bullet was shot)
                                //System.out.println(game.ammo);
                                handler.wait = handler.now + handler.del;   //Waiting time for next viable Input
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
        //checkIfExited(e.getPoint());
    }

    public void mouseExited(MouseEvent e) {
        /*
        //System.out.println("MOUSE EXITED");
        if (Game.getState() == GameState.LEVEL) {
            Game.setState(GameState.PAUSE_MENU);
        }
        */
    }

    /*
    This function checks, if the mouse cursor is outside the window minus the offset and sets the cursors position
    back into the window if previously mentioned event occurs.
    The offset is needed, as in the millisecond the cursor may be outside the window, a click could be input by
    the user, which would result in the loss of focus on the game window.
    This means that the offset minimizes the area of the window, in which the mouse can operate, but only by a few pixels.
    The repositioning of the mouse gets executed via the Robot class, which is mainly used for "user simulations", meaning
    that it is used to simulate key or mouse inputs or even to take screenshots.
    As of right now, the mouse gets checked for eight possible locations:
        ONLY right of window, below window AND right of window, ONLY below window, ...
    (think of the directions like East, South-East, South, ...)
    Further info can be found in the comments in the function
     */
    public void checkIfExited(Point mousePos) {
        // Robot class can apparently throw some errors, so a try and catch is necessary
        try {
            // declares and initializes new robot
            Robot robot = new Robot();
            int offset = 10;
            // saves the window borders as ints for better readability
            // game.getLocationOnScreen() = the position of the top-left corner of the game window on the whole screen
            // game.getHeight() = the height of the game window
            // game.getWidth() = the width of the game window
            int bottomSide = game.getLocationOnScreen().y + game.getHeight() - offset;
            int topSide = game.getLocationOnScreen().y + offset;
            int rightSide = game.getLocationOnScreen().x + game.getWidth() - offset;
            int leftSide = game.getLocationOnScreen().x + offset;

            // mouse pointer is below window
            if (mousePos.y >= bottomSide && (mousePos.x < rightSide && mousePos.x > leftSide)) {
                robot.mouseMove(mousePos.x, bottomSide);
            }

            // mouse pointer is above window
            if (mousePos.y <= topSide && (mousePos.x < rightSide && mousePos.x > leftSide)) {
                robot.mouseMove(mousePos.x, topSide);
            }

            // mouse pointer is right of window
            if (mousePos.x >= rightSide && (mousePos.y < bottomSide && mousePos.y > topSide)) {
                robot.mouseMove(rightSide, mousePos.y);
            }

            // mouse pointer is left of window
            if (mousePos.x <= leftSide && (mousePos.y < bottomSide && mousePos.y > topSide)) {
                robot.mouseMove(leftSide, mousePos.y);
            }

            // ---
            // diagonal

            // is below and right of window
            if (mousePos.y >= bottomSide && mousePos.x >= rightSide) {
                robot.mouseMove(rightSide, bottomSide);
            }

            // is below and left of window
            if (mousePos.y >= bottomSide && mousePos.x <= leftSide) {
                robot.mouseMove(leftSide, bottomSide);
            }

            // is above and right of window
            if (mousePos.y <= topSide && mousePos.x >= rightSide) {
                robot.mouseMove(rightSide, topSide);
            }

            // is above and left of window
            if (mousePos.y <= topSide && mousePos.x <= leftSide) {
                robot.mouseMove(leftSide, topSide);
            }
        } catch (AWTException ex) {
            ex.printStackTrace();
        }
    }

    //endregion
    //region MouseWheelEvents
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            gunequiperror = MouseWheelUp();
            if (game.getState() == GameState.GAME_OVER || game.getState() == GameState.PAUSE_MENU) {
            } else {
                playSoundEquip(gunequiperror);
            }
        } else {
            gunequiperror = MouseWheelDown();
            if (game.getState() == GameState.GAME_OVER || game.getState() == GameState.PAUSE_MENU) {
            } else {
                playSoundEquip(gunequiperror);
            }
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
                    // ex.printStackTrace();
                    //System.out.println("ILLEGAL");
                }
            }).start();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void playSoundEquip(boolean error) {
        try {
            new Thread(() -> {

                try {
                    handler.playSoundEquip(error);
                } catch (LineUnavailableException ex) {
                    ex.printStackTrace();
                } catch (UnsupportedAudioFileException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
        if (game.ammo <= 0) {
            playSoundGun(game.ammo); //has no ammo
        }
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
            temp.direction(mx, my, px, py, false, 0); //Calculate the direction of this bullet
            handler.addObject(temp);    //Add the Bullet to the ObjectList
            playSoundGun(game.ammo);
            game.ammo--;    //Subtract 1 from ammo (bullet was shot)
            handler.wait = handler.now + handler.del;   //Waiting time for next viable Input
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void shotgun(MouseEvent e) {
        handler.now = System.currentTimeMillis();
        //IF waiting time is over AND player has ammo -> shoot a bullet
        if (handler.now > handler.wait && game.ammo >= 1) {
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
            temp.direction(mx, my, px, py, true, 0); //Calculate the direction of this bullet
            handler.addObject(temp);    //Add the Bullet to the ObjectList

            Bullet temp2 = new Bullet((int) px - 4, (int) py - 4, ID.Bullet, handler, an);
            temp2.direction(mx, my, px, py, true, 10); //Calculate the direction of this bullet
            handler.addObject(temp2);    //Add the Bullet to the ObjectList

            Bullet temp3 = new Bullet((int) px - 4, (int) py - 4, ID.Bullet, handler, an);
            temp3.direction(mx, my, px, py, true, -10); //Calculate the direction of this bullet
            handler.addObject(temp3);    //Add the Bullet to the ObjectList
            playSoundGun(game.ammo);
            if (game.ammo <= 3) {
                game.ammo = 0;
            } else {
                game.ammo = game.ammo - 3;    //Subtract 1 from ammo (bullet was shot)
            }
            //System.out.println(game.ammo);
            handler.wait = handler.now + handler.del;   //Waiting time for next viable Input
        } else if (handler.now > handler.wait && game.ammo <= 0) {
            playSoundGun(game.ammo); //has no ammo
            handler.wait = handler.now + handler.del;
        }
    }

    private boolean MouseWheelUp() {
        if (handler.gunindex == gun.guns.size() - 1 || checkNextGunLocked()) {
            return gunequiperror = true;

        } else {
            handler.gunindex++;
        }
        handler.selectedgun = gun.guns.get(handler.gunindex);
        return gunequiperror = false;
    }

    private boolean MouseWheelDown() {
        if (handler.gunindex == 0 || checkPreviousGunLocked()) {
            return gunequiperror = true;

        } else {
            handler.gunindex--;
        }
        handler.selectedgun = gun.guns.get(handler.gunindex);
        return gunequiperror = false;
    }

    private boolean checkNextGunLocked() {
        int index = handler.gunindex + 1;
        Gun nextGun = gun.guns.get(index);
        if (nextGun.isLocked()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkPreviousGunLocked() {
        int index = handler.gunindex - 1;
        Gun previousGun = gun.guns.get(index);
        if (previousGun.isLocked()) {
            return true;
        } else {
            return false;
        }
    }
//endregion
}
