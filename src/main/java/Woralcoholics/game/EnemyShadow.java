package Woralcoholics.game;

import java.awt.*;
import java.awt.image.BufferedImage;

/***
 * The shadow of the enemy if he is dead
 */
public class EnemyShadow extends GameObject {

    private BufferedImage shadow_image = null;
    private Animations enemyDead;

    public EnemyShadow(int x, int y, ID id, ImageGetter an) {
        super(x, y, id, an);
        enemyDead = new Animations(1, Game.enemyDeadShadow[0], Game.enemyDeadShadow[1], Game.enemyDeadShadow[2], Game.enemyDeadShadow[3], Game.enemyDeadShadow[4],Game.enemyDeadShadow[5]);
        //shadow_image = an.getImage(1, 9, 64, 64);
    }

    // create should do nothing in update is a static piece
    public void update() {
        enemyDead.runAnimationsOnce(6);

    }


    public void render(Graphics g) {
        g.drawImage(shadow_image, (int) x, (int) y, null);
        enemyDead.renderAnimation(g, (int) x, (int) y, 64, 64);
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, 32, 32);
    }
}