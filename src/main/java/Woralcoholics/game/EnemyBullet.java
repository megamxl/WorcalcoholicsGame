package Woralcoholics.game;

/**
 * constructs a bullet
 */
public class EnemyBullet extends Bullet{
    public EnemyBullet(int x, int y, ID id, GameManager handler, ImgaeGetter an) {
        super(x, y, id, handler, an);
    }

    /*@Override
    public void render(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval((int) x, (int) y, 8, 8);
    }*/
}
