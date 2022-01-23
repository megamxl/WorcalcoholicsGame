package Woralcoholics.game;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The shadow of the enemy if he is dead
 *
 * @author Christoph Oprawill
 */

public class EnemyShadow extends GameObject {

    private Animations enemyDead;
    public boolean isDone = true;

    public EnemyShadow(int x, int y, ID id, ImageGetter an) {
        super(x, y, id, an);
        enemyDead = new Animations(1, Game.enemyDeadShadow[0], Game.enemyDeadShadow[1], Game.enemyDeadShadow[2], Game.enemyDeadShadow[3], Game.enemyDeadShadow[4], Game.enemyDeadShadow[5], Game.enemyDeadShadow[6]);
    }

    // create should do nothing in update is a static piece
    public void update() {
        EnemyShadow();
    }

    public void render(Graphics g) {
        if (isDone) {
            enemyDead.renderAnimation(g, (int) x, (int) y, 64, 64);  //if animation is finished it doesn't get rendered
            // GameObject tempObject = Game.enemyShadowPool.get(1);
            //tempObject.render(g);
        }
    }

    private void EnemyShadow() {
        if (isDone) {
            isDone = enemyDead.runAnimationsOnce(7); // if animation is finished it doesn't get into
            //System.out.println("updated");
        }
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, 32, 32);
    }
}