package Woralcoholics.game;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Christoph Oprawill
 */

public class DestroyableBoxes extends GameObject {

    private BufferedImage destroyable_boxes;
    private int maxHp = 100;
    private int hp = 100;
    private GameManager manager;
    private boolean fullycracked = false;

    /**
     * Creates the Destroyablebox
     *
     * @param x
     * @param y
     * @param id
     * @param an
     * @param col
     * @param row
     */
    public DestroyableBoxes(float x, float y, ID id, GameManager manager, ImageGetter an, Integer col, Integer row) {
        super(x, y, id, an);
        this.manager = manager;
        // gets the image from the specified column and row from the spritesheet
        destroyable_boxes = an.getImage(col, row, 64, 64);
        hp = maxHp;
    }

    public void update() {
        collision();
        isCracked();
    }

    public void render(Graphics g) {
        g.drawImage(destroyable_boxes, (int) x, (int) y, null);
        /*Graphics2D g2d = (Graphics2D) g;
        g.setColor(Color.GREEN);
        g2d.draw(getBounds());*/

    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, 64, 64);
    }

    public static void destroyBox(GameManager handler, GameObject tmp) {
        handler.removeObject(tmp);
    }

    /***
     * If a Object in our List collides with the Box
     */
    public void collision() {
        /*for (int i = 0; i < manager.object.size(); i++) {

            GameObject tmpObject = manager.object.get(i);


            if (tmpObject.getId() == ID.Bullet) {
                if (getBounds().intersects(tmpObject.getBounds())) {
                    manager.removeObject(tmpObject);
                    boxDestroyedSound();
                    if (manager.selectedgun.getType() == GunType.Pistol)
                        hp -= 40; //could be also random in a specific range
                    else if (manager.selectedgun.getType() == GunType.Shotgun)
                        hp -= 30;
                    else //
                        hp -= 20;
                    crackedState();
                    //System.out.println(hp);
                }
            }
            if (tmpObject.getId() == ID.EnemyBullet) {
                if (getBounds().intersects(tmpObject.getBounds())) {
                    manager.removeObject(tmpObject);
                    boxDestroyedSound();
                    hp -= 20;
                    crackedState();
                    //System.out.println(hp);
                }
            }
        }*/
        for(int i = 0; i < manager.bullets.size(); i++) {
            Bullet temp = manager.bullets.get(i);
            if (getBounds().intersects(temp.getBounds())) {
                switch(temp.getId()) {
                    case Bullet -> {
                        //manager.removeObject(temp);
                        temp.inGame = false;
                        temp.setPos(0,0);
                        boxDestroyedSound();
                        if (manager.selectedgun.getType() == GunType.Pistol)
                            hp -= 40; //could be also random in a specific range
                        else if (manager.selectedgun.getType() == GunType.Shotgun)
                            hp -= 30;
                        else //
                            hp -= 20;
                        crackedState();
                    }
                    case EnemyBullet -> {
                        //manager.removeObject(temp);
                        temp.setId(ID.Bullet);
                        temp.inGame = false;
                        temp.setPos(0,0);
                        boxDestroyedSound();
                        hp -= 20;
                        crackedState();
                    }
                }
            }
        }
    }

    /***
     * Check if Box is Cracked
     */
    private void isCracked() {
        if (hp <= 0) {
            remove();
            fullycracked = true;
        }
    }

    // 5 Cracksprites -> 100/5 = 20 (100,80,60,40,20)
    private void crackedState() {
        //100 standard setted in game
        if (hp <= 80)
            destroyable_boxes = an.getImage(4, 3, 64, 64);
        if (hp <= 60)
            destroyable_boxes = an.getImage(5, 3, 64, 64);
        if (hp <= 40)
            destroyable_boxes = an.getImage(6, 3, 64, 64);
        if (hp <= 20)
            destroyable_boxes = an.getImage(7, 3, 64, 64);
    }

    /***
     * Remove the Box
     */
    public void remove() {
        manager.removeObject(this);
    }

    /***
     * Runs the sound if box cracks
     */
    private void boxDestroyedSound() {
        try {
            new Thread(() -> {

                try {
                    manager.playSoundDestroyedBox(fullycracked);
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                } catch (UnsupportedAudioFileException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    // e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}