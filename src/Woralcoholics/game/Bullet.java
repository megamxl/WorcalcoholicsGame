package Woralcoholics.game;

import java.awt.*;

public class Bullet extends GameObject{

    private float bulletSpeed = 1;

    GameManager handler;
    Game game;

    public Bullet(int x, int y, ID id, GameManager handler, Game game) {
        super(x, y, id);
        this.handler = handler;
        this.game = game;
    }


    public void direction(double mx, double my, double px, double py) {
        //double c = Math.sqrt(Math.pow(mx,2) + Math.pow(my,2));
        double dx = mx-px;
        double dy = my-py;
        double alpha = Math.atan2(dy, dx);
        System.out.println(dx + " " + dy);
        //System.out.println(alpha);
        velX = (float) (Math.cos(alpha) * bulletSpeed);
        velY = (float) (Math.sin(alpha) * bulletSpeed);
        /*
        velX = (float) (bulletSpeed * mx/c);
        velY = (float) (bulletSpeed * my/c);*/

        //System.out.println(velX + " " + velY);
    }

    public void collision() {   //Collision Detection (Enemys, Blocks)
        for(int i = 0; i < handler.object.size(); i++ ) {
            GameObject tmpObject = handler.object.get(i);

            if (tmpObject.getId() == ID.Block || tmpObject.getId() == ID.Enemy) {
                if(this.getBounds().intersects(tmpObject.getBounds())) {
                    handler.removeObject(this);
                    System.out.println("Collision");
                }
            }
        }
    }

    public void ooB() {
        for(int i = 0; i < handler.object.size(); i++) {
            GameObject tmp = handler.object.get(i);

            if(tmp.getId() == ID.Bullet) {
                if(x-4 > 2000 || y-4 > 1000 || x < 0 || y < 0) {   //Out of Bounds
                    handler.removeObject(this);
                    System.out.println("OoB");
                }
            }
        }
    }

    @Override
    public void update() {
        x += velX;
        y += velY;

        collision();
        ooB();

    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillOval((int)x, (int)y, 8, 8);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int)x,(int)y, 8,8);
    }

    public float getBulletSpeed() {
        return bulletSpeed;
    }

    public void setBulletSpeed(float bs) {
        bulletSpeed = bs;
    }
}

