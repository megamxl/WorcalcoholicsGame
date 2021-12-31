package Woralcoholics.game;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
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
    private Gun gun;
    volatile private boolean mouseDown = false; //determine if mouse1 is pressed or not
    private boolean gunequiperror = false;
    Upgrades upgrades;


    public MouseInput(GameManager handler, Camera camera, Game game, Animations an, Gun gun) {
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
            case TITLE -> {
                if (button == 1) Game.setState(GameState.MAIN_MENU);
            }
            case MAIN_MENU, HIGH_SCORES, OPTIONS, PAUSE_MENU -> {
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
                //if (button == 1) Game.setState(GameState.LEVEL);
                int[] randomUpgrades = upgrades.getUpgrades();
                if (button == 1) {
                    if (e.getX() >= 137 && e.getX() <= 457 && e.getY() >= 30 && e.getY() <= 630) { //button 1
                        upgrades.getUpgrade(randomUpgrades[0]);
                        Upgrades.drawRandomUpgrades(); //draw random 3 upgrades for 2nd, 3rd... time buying them
                        Game.setState(GameState.LEVEL);
                    } else if (e.getX() >= 377 && e.getX() <= 697 && e.getY() >= 30 && e.getY() <= 630) { //button 2
                        upgrades.getUpgrade(randomUpgrades[1]);
                        Upgrades.drawRandomUpgrades();
                        Game.setState(GameState.LEVEL);
                    } else if (e.getX() >= 617 && e.getX() <= 937 && e.getY() >= 30 && e.getY() <= 630) { //button 3
                        upgrades.getUpgrade(randomUpgrades[2]);
                        Upgrades.drawRandomUpgrades();
                        Game.setState(GameState.LEVEL);
                    }
                }
                Game.TimerValue = 5;    //5 secs wait time
                Game.shouldTime = true; //activate Timer
                Game.timerAction = 1;   //execute timerAction 1 -> countdown till next wave
            }
            case LEVEL, TUTORIAL -> {
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
            gunequiperror = MouseWheelUp();
            playSoundEquip(gunequiperror);
        } else {
            gunequiperror = MouseWheelDown();
            playSoundEquip(gunequiperror);
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

            if (game.ammo <= 3) {
                game.ammo = 0;
            } else {
                game.ammo = game.ammo - 3;    //Subtract 1 from ammo (bullet was shot)
            }
            //System.out.println(game.ammo);
            handler.wait = handler.now + handler.del;   //Waiting time for next viable Input
            playSoundGun(game.ammo);
        } else if (handler.now > handler.wait && game.ammo <= 0) {
            playSoundGun(game.ammo); //has no ammo
            handler.wait = handler.now + handler.del;
        }
    }

    private boolean MouseWheelUp() {
        if (handler.gunindex == gun.guns.size() - 1) { // 3 values -> 0 to 2
            return gunequiperror = true;

        } else {
            handler.gunindex++;
        }
        //gun.guns.indexOf(handler.gunindex);
        handler.selectedgun = gun.guns.get(handler.gunindex);
        return gunequiperror = false;
    }

    private boolean MouseWheelDown() {
        if (handler.gunindex == 0) {
            return gunequiperror = true;

        } else {
            handler.gunindex--;
        }
        handler.selectedgun = gun.guns.get(handler.gunindex);
        return gunequiperror = false;
    }
//endregion
}
