package Woralcoholics.game;

import java.awt.*;
import java.util.Random;


public class Enemy extends GameObject {

    //Handler is for collision detection
    private GameManager manager;
    Random r = new Random();
    int choose = 0;
    int hp = 100;
    int low = -4;
    int high = 4;

    public Enemy(int x, int y, ID id,GameManager manager, Animations an) {
        super(x, y, id, an);
        this.manager = manager;
    }


    public void update() {
        x += velX;
        y += velY;

        choose = r.nextInt(10);

        for (int i = 0; i < manager.object.size(); i++) {

            GameObject tmpObject = manager.object.get(i);

            //if it's coliding with a block
            if (tmpObject.getId() == ID.Block) {
                if (getBoundsAround().intersects(tmpObject.getBounds())) {
                    // if it coliding with wall it goes in the opposite direction
                    x += (velX * 5) * -1;                                       // maybe improve these values(velx*5,velxy*= -1)
                    y += (velY * 5) * -1;
                    velX *= -1;
                    velY *= -1;
                } else if (choose == 0) {
                    //moving between 4 and -4 per iterate
                    velX = r.nextInt(high - low) + low;
                    velY = r.nextInt(high - low) + low;
                }


            }

            //if our bullet is coliding with the enemy hp get's -50
            if (tmpObject.getId() == ID.Bullet) {
                if (getBounds().intersects(tmpObject.getBounds())) {
                    hp -= 100;
                    manager.removeObject(tmpObject);
                }
            }

        }

        if (hp <= 0) {
            manager.removeObject(this);
        }
    }

    //Enemy is displaying in this Color for now
    public void render(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillRect((int)x, (int)y, 32, 32);

         //-> Just for testing Bounding Boxes around the Enemys
        /*
        Graphics2D g2d = (Graphics2D) g;
        g.setColor(Color.GREEN);
        g2d.draw(getBoundsAround());

         */


    }

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, 32, 32);
    }

    public Rectangle getBoundsAround() {
        return new Rectangle((int)x - 16, (int)y - 16, 64, 64);
    }
}
