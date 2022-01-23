package Woralcoholics.game;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The hitmarker of the enemy if he is dead
 *
 * @author Christoph Oprawill
 */

public class EnemyHitmarker extends GameObject {

    private Animations enemyHitmarker;
    public boolean isDone = true;

    public EnemyHitmarker(int x, int y, ID id, ImageGetter an) {
        super(x, y, id, an);
        enemyHitmarker = new Animations(0, Game.enemyHitmarker[0], Game.enemyHitmarker[1], Game.enemyHitmarker[2], Game.enemyHitmarker[3]);
    }

    // create should do nothing in update is a static piece
    public void update() {
        if (isDone) {
            isDone = enemyHitmarker.runAnimationsOnce(4);
        }
    }

    public void render(Graphics g) {
        if (isDone) {
            enemyHitmarker.renderAnimation(g, (int) x, (int) y, 64, 64);  //if animation is finished it doesn't get rendered
            // GameObject tempObject = Game.enemyShadowPool.get(1);
            //tempObject.render(g);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, 32, 32);
    }
}